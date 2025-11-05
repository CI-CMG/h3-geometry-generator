package edu.colorado.cires.cmg.geometry_generator.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.colorado.cires.cmg.geometry_generator.reader.tif.CloseableGeotiffReader;
import edu.colorado.cires.cmg.geometry_generator.reader.tif.GeoTiffCoordinateReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.geotools.api.coverage.grid.GridCoverage;
import org.geotools.api.referencing.FactoryException;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

class GeoTiffCoordinateReaderTest {

  private final Path testDir = Paths.get("target/test-data");

  @BeforeEach
  void setUp() throws IOException {
    FileUtils.forceMkdir(testDir.toFile());
  }

  @AfterEach
  void tearDown() {
    FileUtils.deleteQuietly(testDir.toFile());
  }

  @Test
  void read() throws IOException, FactoryException {
    Path file = testDir.resolve("test.tif");
    GeoTiffWriter geoTiffWriter = new GeoTiffWriter(file.toFile());

    int rows = 5;
    int cols = 4;

    try {
      float[][] data = new  float[rows][cols];
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
          data[i][j] = ((float) (i + j) / (rows * cols)) * 255;
        }
      }

      data[0][cols - 1] = 0;
      data[rows - 1][cols - 1] = 255;
      data[rows - 1][0] = 255;

      GridCoverage gridCoverage = new GridCoverageFactory().create("TestGrid", data, new ReferencedEnvelope(0, 1, 2, 3, CRS.decode("EPSG:4326")));
      geoTiffWriter.write(gridCoverage, null);
    } finally {
      geoTiffWriter.dispose();
    }

    try (CloseableGeotiffReader reader = new CloseableGeotiffReader(ImageIO.createImageInputStream(file.toFile()))) {
      List<Coordinate> resultCoordinates = GeoTiffCoordinateReader.read(reader).toList();
      // should be missing corners
      assertEquals(
        (rows * cols) - 4,
        resultCoordinates.size()
      );
      assertTrue(
        resultCoordinates.stream().noneMatch(c ->
          List.of(
            new Coordinate(0, 2),
            new Coordinate(1, 2),
            new Coordinate(1, 3),
            new Coordinate(0, 3)
          ).contains(c)
        )
      );
    }
  }
}