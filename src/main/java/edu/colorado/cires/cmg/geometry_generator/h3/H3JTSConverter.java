package edu.colorado.cires.cmg.geometry_generator.h3;

import com.uber.h3core.H3Core;
import java.util.function.Function;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

/**
 * Converts between H3 outputs and JTS outputs
 */
public interface H3JTSConverter {

  /**
   * Converts a JTS {@link Coordinate} to an H3 cell
   * @param coordinate {@link Coordinate} to convert
   * @return H3 cell
   */
  long coordinateToCell(Coordinate coordinate);

  /**
   * Converts an H3 cell to a JTS {@link Polygon}
   * @param cell H3 cell
   * @return JTS {@link Polygon} representing H3 cell hexagon
   */
  Polygon cellToPolygon(long cell);

  /**
   * Factory method for creating a default {@link H3JTSConverter}
   * @param h3Core {@link H3Core} for generating / analyzing H3 cells
   * @param resolution H3 resolution assumed by resulting converter (0-15)
   * @param geometryFactory {@link Function} for creating a {@link Geometry} from an array of {@link Coordinate}
   * @return The default implementation of {@link H3JTSConverter}
   */
  static H3JTSConverter create(H3Core h3Core, int resolution, Function<Coordinate[], Geometry> geometryFactory) {
    return new H3JTSConverterImpl(resolution, h3Core, geometryFactory);
  }

}
