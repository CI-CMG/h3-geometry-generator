package edu.colorado.cires.cmg.geometry_generator.reader.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.function.FailableFunction;
import org.locationtech.jts.geom.Coordinate;

/**
 * Reads coordinates from csv files
 */
public class CSVCoordinateReader {

  private final Function<CSVRecord, String> longitudeResolver;
  private final Function<CSVRecord, String> latitudeResolver;
  private final char delimiter;

  /**
   * Creates a {@link CSVCoordinateReader}
   * @param longitudeResolver header name for longitude values
   * @param latitudeResolver header name for latitude values
   * @param delimiter csv delimiter
   */
  public CSVCoordinateReader(Function<CSVRecord, String> longitudeResolver, Function<CSVRecord, String> latitudeResolver, char delimiter) {
    this.longitudeResolver = longitudeResolver;
    this.latitudeResolver = latitudeResolver;
    this.delimiter = delimiter;
  }

  /**
   * Creates a {@link Stream<Coordinate>} from a{@link Reader}
   * @param reader csv {@link Reader}
   * @return {@link Stream<Coordinate>} containing coordinates from csv file
   * @throws IOException if csv file cannot be read
   */
  public Stream<Coordinate> read(Reader reader) throws IOException {
    CSVFormat format = CSVFormat.DEFAULT.builder()
      .setHeader()
      .setSkipHeaderRecord(true)
      .setDelimiter(delimiter)
      .build();

    return format.parse(reader).stream()
      .map(record -> new Coordinate(
        Double.parseDouble(longitudeResolver.apply(record)),
        Double.parseDouble(latitudeResolver.apply(record))
      ));
  }

}
