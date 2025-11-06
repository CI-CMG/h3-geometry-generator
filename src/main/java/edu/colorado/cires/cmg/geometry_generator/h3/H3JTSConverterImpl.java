package edu.colorado.cires.cmg.geometry_generator.h3;

import com.uber.h3core.H3Core;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

/**
 * Default implementation of {@link H3JTSConverter}
 */
class H3JTSConverterImpl implements H3JTSConverter {

  private final int resolution;
  private final H3Core h3Core;
  private final Function<Coordinate[], Polygon> geometryFactory;

  /**
   * Creates a {@link H3JTSConverterImpl}
   * @param h3Core {@link H3Core} for generating / analyzing H3 cells
   * @param resolution H3 resolution assumed by resulting converter (0-15)
   * @param geometryFactory {@link Function} for creating a {@link Polygon} from an array of {@link Coordinate}
   */
  H3JTSConverterImpl(int resolution, H3Core h3Core, Function<Coordinate[], Polygon> geometryFactory) {
    this.resolution = resolution;
    this.h3Core = h3Core;
    this.geometryFactory = geometryFactory;
  }

  /**
   * Converts a JTS {@link Coordinate} to an H3 cell
   * @param coordinate {@link Coordinate} to convert
   * @return H3 cell
   */
  @Override
  public long coordinateToCell(Coordinate coordinate) {
    return h3Core.geoToH3(coordinate.getY(), coordinate.getX(), resolution);
  }

  /**
   * Converts an H3 cell to a JTS {@link Polygon}
   * @param cell H3 cell
   * @return JTS {@link Polygon} representing H3 cell hexagon
   */
  @Override
  public Polygon cellToPolygon(long cell) {
    List<Coordinate> coordinates = h3Core.h3ToGeoBoundary(cell).stream()
      .map(geoCoord -> new Coordinate(geoCoord.lng, geoCoord.lat))
      .collect(Collectors.toList());

    coordinates.add(coordinates.get(0));

    return geometryFactory.apply(
      coordinates.toArray(Coordinate[]::new)
    );
  }
}
