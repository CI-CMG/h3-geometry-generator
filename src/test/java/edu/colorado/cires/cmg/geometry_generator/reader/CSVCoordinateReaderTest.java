package edu.colorado.cires.cmg.geometry_generator.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.colorado.cires.cmg.geometry_generator.reader.csv.CSVCoordinateReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

class CSVCoordinateReaderTest {

  @Test
  void read() throws IOException {
    Reader reader = new StringReader("""
    LNG,LAT
    1.0,2.0
    3.0,4.0
    """);

    CSVCoordinateReader csvReader = new CSVCoordinateReader(csv -> csv.get(0), csv -> csv.get(1), ',');

    assertEquals(
      Set.of(
        new Coordinate(1, 2),
        new Coordinate(3, 4)
      ),
      csvReader.apply(reader)
        .collect(Collectors.toSet())
    );
  }
}