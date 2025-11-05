package edu.colorado.cires.cmg.geometry_generator.reducer;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.BinaryOperator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

class DouglasPeuckerReducerTest {

  private final GeometryFactory geometryFactory = new GeometryFactory();

  private final int maxPointCount = 10;

  private final BinaryOperator<Geometry> operator = new DouglasPeuckerReducer(
    maxPointCount,
    0.1,
    0.01
  );

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 4, 8, 16, 32, 64, 128})
  void apply(int nPolygons) {
    Geometry geometry = geometryFactory.createEmpty(2);

    for (int i = 0; i < nPolygons; i++) {
      geometry = operator.apply(
        geometry,
        geometryFactory.createPolygon(new Coordinate[]{
          new Coordinate(i * 0.1, i * 0.1),
          new Coordinate(1 + i * 0.1, i * 0.1),
          new Coordinate(1 + i * 0.1, -1 + i * 0.1),
          new Coordinate(i * 0.1, -1 + i * 0.1),
          new Coordinate(i * 0.1, i * 0.1),
        })
      );
    }

    assertInstanceOf(Polygon.class, geometry);
    assertTrue(geometry.isValid());
    assertTrue(geometry.getCoordinates().length < 10);


  }
}