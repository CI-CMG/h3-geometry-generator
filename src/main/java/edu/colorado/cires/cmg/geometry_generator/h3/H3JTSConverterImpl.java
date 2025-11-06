package edu.colorado.cires.cmg.geometry_generator.h3;

import com.uber.h3core.H3Core;
import com.uber.h3core.util.GeoCoord;
import java.util.List;
import java.util.function.Function;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

/**
 * Default implementation of {@link H3JTSConverter}
 */
class H3JTSConverterImpl implements H3JTSConverter {

  private final int resolution;
  private final H3Core h3Core;
  private final GeometryFactory geometryFactory;

  /**
   * Creates a {@link H3JTSConverterImpl}
   * @param h3Core {@link H3Core} for generating / analyzing H3 cells
   * @param resolution H3 resolution assumed by resulting converter (0-15)
   * @param geometryFactory {@link Function} for creating a {@link Polygon} from an array of {@link Coordinate}
   */
  H3JTSConverterImpl(int resolution, H3Core h3Core, GeometryFactory geometryFactory) {
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
   * Converts a list of H3 cells to a JTS {@link MultiPolygon}
   * @param cells list of H3 cells
   * @return {@link MultiPolygon} representing the union of H3 cell hexagons from the input cell list
   */
  @Override
  public Geometry cellsToMultiPolygon(List<Long> cells) {
    return geometryFactory.createMultiPolygon(
      h3Core.h3SetToMultiPolygon(cells, true).stream()
        .map(polygonCoordinates -> geometryFactory.createPolygon(
          geometryFactory.createLinearRing(
            polygonCoordinates.get(0).stream()
              .map(this::coordinateFromGeoCoord)
              .toArray(Coordinate[]::new)
          ),
          polygonCoordinates.size() == 1 ? new LinearRing[0] : polygonCoordinates.subList(1, polygonCoordinates.size()).stream()
            .map(geoCoords -> geometryFactory.createLinearRing(geoCoords.stream().map(this::coordinateFromGeoCoord).toArray(Coordinate[]::new)))
            .toArray(LinearRing[]::new)
        )).toArray(Polygon[]::new)
    );
  }

  private Coordinate coordinateFromGeoCoord(GeoCoord geoCoord) {
    return new Coordinate(geoCoord.lng, geoCoord.lat);
  }
}
