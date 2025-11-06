package edu.colorado.cires.cmg.geometry_generator.collector;

import java.util.List;
import java.util.function.Function;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;

/**
 * Extension of {@link CellCollector} which assumes the same underlying behavior with further simplification occurring during the collection process.
 * Simplification occurs via the Douglas-Peucker algorithm.
 */
public class DouglasPeuckerCellCollector extends CellCollector {

  private Geometry result = null;
  private final int maximumPoints;
  private final double distanceTolerance;
  private final double distanceToleranceDelta;

  /**
   * Creates a {@link DouglasPeuckerCellCollector}
   * @param cellConverter for transforming a list of H3 cells to a JTS geometry
   * @param maximumPoints maximum points allowed in resulting geometry
   * @param distanceTolerance distance tolerance for Douglas-Peucker algorithm
   * @param distanceToleranceDelta how much to increase Douglas-Peucker algorithm distance tolerance by when maximum geometry points has been exceeded
   */
  public DouglasPeuckerCellCollector(Function<List<Long>, Geometry> cellConverter,
    int maximumPoints, double distanceTolerance, double distanceToleranceDelta) {
    super(cellConverter);
    this.maximumPoints = maximumPoints;
    this.distanceTolerance = distanceTolerance;
    this.distanceToleranceDelta = distanceToleranceDelta;
  }

  /**
   * Transforms list of H3 cells to JTS geometry. If points in the resulting geometry exceed the allowed value, the geometry is simplified iteratively until point count is lowered below the acceptable value.
   * @return {@link Function} for transforming list of H3 cells to JTS geometry
   */
  @Override
  public Function<List<Long>, Geometry> finisher() {
    return cells ->  {
      Geometry geometry = super.finisher().apply(cells);

      if (result == null) {
        result = geometry;
      } else {
        result = result.union(geometry);
      }

      double localDistanceTolerance = distanceTolerance;

      while (result.getNumPoints() > maximumPoints) {
        result = DouglasPeuckerSimplifier.simplify(result, localDistanceTolerance);
        localDistanceTolerance += distanceToleranceDelta;
      }

      return result;
    };
  }
}
