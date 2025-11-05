package edu.colorado.cires.cmg.geometry_generator.reader.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.lang3.function.FailableFunction;
import org.locationtech.jts.geom.Coordinate;

/**
 * Reads coordinates from csv files
 */
public class CSVCoordinateReader implements FailableFunction<Reader, Stream<Coordinate>, IOException> {

  private final String longitudeHeader;
  private final String latitudeHeader;
  private final char delimiter;

  /**
   * Creates a {@link CSVCoordinateReader}
   * @param longitudeHeader header name for longitude values
   * @param latitudeHeader header name for latitude values
   * @param delimiter csv delimiter
   */
  public CSVCoordinateReader(String longitudeHeader, String latitudeHeader, char delimiter) {
    this.longitudeHeader = longitudeHeader;
    this.latitudeHeader = latitudeHeader;
    this.delimiter = delimiter;
  }

  /**
   * Creates a {@link Stream<Coordinate>} from a{@link Reader}
   * @param reader csv {@link Reader}
   * @return {@link Stream<Coordinate>} containing coordinates from csv file
   * @throws IOException if csv file cannot be read
   */
  @Override
  public Stream<Coordinate> apply(Reader reader) throws IOException {
    CSVFormat format = CSVFormat.DEFAULT.builder()
      .setHeader()
      .setSkipHeaderRecord(true)
      .setDelimiter(delimiter)
      .build();

    return format.parse(reader).stream()
      .map(record -> {
        Map<String, String> rowData = record.toMap();

        return new Coordinate(
          Double.parseDouble(rowData.get(longitudeHeader)),
          Double.parseDouble(rowData.get(latitudeHeader))
        );
      });
  }

}
