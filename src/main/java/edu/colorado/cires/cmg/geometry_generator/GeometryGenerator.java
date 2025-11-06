package edu.colorado.cires.cmg.geometry_generator;

import edu.colorado.cires.cmg.geometry_generator.collector.CellCollector;
import edu.colorado.cires.cmg.geometry_generator.h3.H3JTSConverter;
import java.io.IOException;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.commons.lang3.function.FailableFunction;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

/**
 * Reads coordinates from a data source, bins them into H3 cells, and collects H3 indices into a MultiPolygon
 */
public class GeometryGenerator {
  private final H3JTSConverter h3JTSConverter;
  private final Supplier<CellCollector> cellCollectorSupplier;

  /**
   * Creates a {@link GeometryGenerator}
   * @param h3JTSConverter {@link H3JTSConverter} for converting between H3 outputs and JTS outputs
   * @param cellCollectorSupplier {@link Supplier<CellCollector>} for collecting H3 cells into a MultiPolygon
   */
  public GeometryGenerator(H3JTSConverter h3JTSConverter, Supplier<CellCollector> cellCollectorSupplier) {
    this.h3JTSConverter = h3JTSConverter;
    this.cellCollectorSupplier = cellCollectorSupplier;
  }

  /**
   * Performs the following workflow:
   * 1. Reads coordinates from a source
   * 2. Converts coordinates to H3 indices
   * 3. Collects H3 indices into a MultiPolygon
   * @param source {@link T} containing coordinates
   * @param fileReader {@link FailableFunction} for reading coordinates from source
   * @return {@link Geometry} containing coordinates from source
   * @throws IOException if coordinates cannot be read from source
   */
  public <T> Geometry generate(T source, FailableFunction<T, Stream<Coordinate>, IOException> fileReader) throws IOException {
    return fileReader.apply(source)
      .map(h3JTSConverter::coordinateToCell)
      .distinct()
      .collect(cellCollectorSupplier.get());
  }
}
