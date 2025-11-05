package edu.colorado.cires.cmg.geometry_generator.reducer;

import java.util.function.BinaryOperator;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;

/**
 * Unions JTS {@link Geometry} and performs coordinate simplification using the Douglas-Peucker algorithm
 */
public class DouglasPeuckerReducer implements BinaryOperator<Geometry> {

  private final int maxPointCount;
  private final double distanceTolerance;
  private final double toleranceInterval;

  /**
   * Creates a {@link DouglasPeuckerReducer}
   * @param maxPointCount maximum allowed points in output
   * @param distanceTolerance Douglas-Peucker algorithm distance tolerance
   * @param toleranceInterval interval to increase {@link DouglasPeuckerReducer#distanceTolerance} if maximum points are still violated after initial application of the Douglas-Peucker algorithm
   */
  public DouglasPeuckerReducer(int maxPointCount, double distanceTolerance, double toleranceInterval) {
    this.maxPointCount = maxPointCount;
    this.distanceTolerance = distanceTolerance;
    this.toleranceInterval = toleranceInterval;
  }

  /**
   * Unions JTS {@link Geometry} and performs coordinate simplification using the Douglas-Peucker algorithm
   * @param geometry1 first {@link Geometry} to union
   * @param geometry2 second {@link Geometry} to union
   * @return union of the input geometries which have been simplified by the Douglas-Peucker algorithm
   */
  @Override
  public Geometry apply(Geometry geometry1, Geometry geometry2) {
    double localDistanceTolerance = distanceTolerance;

    Geometry result = geometry1.union(geometry2);

    while (result.getCoordinates().length >= maxPointCount) {
      result = DouglasPeuckerSimplifier.simplify(result, localDistanceTolerance);
      localDistanceTolerance += toleranceInterval;
    }

    return result;
  }
}
