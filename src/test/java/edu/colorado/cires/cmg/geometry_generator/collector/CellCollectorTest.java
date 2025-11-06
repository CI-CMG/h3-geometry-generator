package edu.colorado.cires.cmg.geometry_generator.collector;

import static org.junit.jupiter.api.Assertions.*;

import com.uber.h3core.H3Core;
import edu.colorado.cires.cmg.geometry_generator.h3.H3JTSConverter;
import java.io.IOException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;

class CellCollectorTest {

  private final H3Core h3Core;
  {
    try {
      h3Core = H3Core.newInstance();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private final GeometryFactory geometryFactory = new GeometryFactory();

  private final H3JTSConverter converter = H3JTSConverter.create(h3Core, 8, geometryFactory);

  @Test
  void collect() {
    assertEquals(
      "MULTIPOLYGON (((1.003659095026228 1.0022404330346844, 1.0015982895011577 1.0056602942559627, 0.9975959713024461 1.0050215488435168, 0.995654310251168 1.0009631733946358, 0.9977148374189814 0.9975432553636355, 1.001717304000721 0.9981817695784827, 1.003659095026228 1.0022404330346844)))",
      Stream.of(614552348391374847L)
        .collect(new CellCollector(converter::cellsToMultiPolygon))
        .toText()
    );
  }
}