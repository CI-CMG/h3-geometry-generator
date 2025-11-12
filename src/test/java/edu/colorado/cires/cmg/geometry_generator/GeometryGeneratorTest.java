package edu.colorado.cires.cmg.geometry_generator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.uber.h3core.H3Core;
import edu.colorado.cires.cmg.geometry_generator.collector.DouglasPeuckerCellCollector;
import edu.colorado.cires.cmg.geometry_generator.h3.H3JTSConverter;
import edu.colorado.cires.cmg.geometry_generator.reader.csv.CSVCoordinateReader;
import edu.colorado.cires.cmg.geometry_generator.reader.tif.CloseableGeotiffReader;
import edu.colorado.cires.cmg.geometry_generator.reader.tif.GeoTiffCoordinateReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.locationtech.jts.geom.GeometryFactory;

class GeometryGeneratorTest {

  @ParameterizedTest
  @ValueSource(strings = {"src/test/resources/large_file", "src/test/resources/small_file"})
  void generate(String filePath) throws IOException {
    GeometryFactory geometryFactory = new GeometryFactory();

    try (
      InputStream csvStream = Files.newInputStream(Paths.get("%s.csv".formatted(filePath)));
      Reader csvReader = new InputStreamReader(csvStream);
      ImageInputStream geoTiffStream = ImageIO.createImageInputStream(new File("%s.tif".formatted(filePath)));
      CloseableGeotiffReader geotiffReader = new CloseableGeotiffReader(geoTiffStream)
    ) {
      CSVCoordinateReader csvCoordinateReader = new CSVCoordinateReader(csv -> csv.get(0), csv -> csv.get(1), ',');

      H3JTSConverter converter = H3JTSConverter.create(H3Core.newInstance(), 7, geometryFactory);

      GeometryGenerator geometryGenerator = new GeometryGenerator(
        converter,
        () -> new DouglasPeuckerCellCollector(converter::cellsToMultiPolygon, 1000, 0.01, 0.01)
      );

      assertTrue(
        geometryGenerator.generate(csvCoordinateReader.read(csvReader))
          .difference(
            geometryGenerator.generate(GeoTiffCoordinateReader.read(geotiffReader))
          ).isEmpty()
      );
    }
  }
}