package edu.colorado.cires.cmg.geometry_generator;

import edu.colorado.cires.cmg.geometry_generator.h3.H3JTSConverter;
import java.io.IOException;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.commons.lang3.function.FailableFunction;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

/**
 * Reads coordinates from a data source, bins them into H3 cells, transforms cells into hexagonal polygons, and reduces polygons to a singular geometry
 */
public class GeometryGenerator {
  private final H3JTSConverter h3JTSConverter;
  private final BinaryOperator<Geometry> geometryReducer;
  private final Supplier<Geometry> geometryInitializer;

  /**
   * Creates a {@link GeometryGenerator}
   * @param h3JTSConverter {@link H3JTSConverter} for converting between H3 outputs and JTS outputs
   * @param geometryReducer {@link BinaryOperator<Geometry>} for reducing polygons to a singular geometry
   * @param geometryInitializer {@link Supplier<Geometry>} for initializing the geometry which {@link GeometryGenerator#generate} will return
   */
  public GeometryGenerator(
    H3JTSConverter h3JTSConverter,
    BinaryOperator<Geometry> geometryReducer,
    Supplier<Geometry> geometryInitializer
  ) {
    this.h3JTSConverter = h3JTSConverter;
    this.geometryReducer = geometryReducer;
    this.geometryInitializer = geometryInitializer;
  }

  /**
   * Performs the following workflow:
   * 1. Reads coordinates from a source
   * 2. Converts coordinates to H3 indices
   * 3. Converts H3 indices to hexagonal polygons
   * 4. Reduces polygons to a singular geometry
   * @param source {@link T} containing coordinates
   * @param fileReader {@link FailableFunction} for reading coordinates from source
   * @return {@link Geometry} containing coordinates from source
   * @throws IOException if coordinates cannot be read from source
   */
  public <T> Geometry generate(T source, FailableFunction<T, Stream<Coordinate>, IOException> fileReader) throws IOException {
    return fileReader.apply(source)
      .map(h3JTSConverter::coordinateToCell)
      .distinct()
      .map(h3JTSConverter::cellToPolygon)
      .reduce(
        geometryInitializer.get(),
        geometryReducer,
        geometryReducer
      );
  }
}
