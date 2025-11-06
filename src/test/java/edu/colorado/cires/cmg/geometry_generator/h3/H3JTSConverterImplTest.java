package edu.colorado.cires.cmg.geometry_generator.h3;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.uber.h3core.H3Core;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

class H3JTSConverterImplTest {

  private final long cell = 614552348391374847L;
  private final int resolution = 8;
  private final GeometryFactory geometryFactory = new GeometryFactory();
  private final H3Core h3Core;

  {
    try {
      h3Core = H3Core.newInstance();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private final H3JTSConverter h3JTSConverter = new H3JTSConverterImpl(
    resolution, h3Core, geometryFactory
  );

  @Test
  void coordinateToCell() {
    assertEquals(cell, h3JTSConverter.coordinateToCell(new Coordinate(1, 1)));
  }

  @Test
  void cellsToMultiPolygon() {
    assertEquals(
      geometryFactory.createMultiPolygon(new Polygon[]{
        geometryFactory.createPolygon(
          h3Core.h3SetToMultiPolygon(List.of(cell), true).stream()
            .flatMap(
              l -> l.stream()
                .flatMap(List::stream)
            ).map(geoCoord -> new Coordinate(geoCoord.lng, geoCoord.lat))
            .toArray(Coordinate[]::new)
        )
      }),
      h3JTSConverter.cellsToMultiPolygon(List.of(cell))
    );
  }
}