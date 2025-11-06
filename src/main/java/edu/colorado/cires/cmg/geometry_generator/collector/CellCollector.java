package edu.colorado.cires.cmg.geometry_generator.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import org.locationtech.jts.geom.Geometry;

/**
 * Collects H3 cells into a JTS geometry
 */
public class CellCollector implements Collector<Long, List<Long>, Geometry> {

  private final Function<List<Long>, Geometry> cellConverter;

  /**
   * Creates a {@link CellCollector}
   * @param cellConverter for transforming a list of H3 cells to a JTS geometry
   */
  public CellCollector(Function<List<Long>, Geometry> cellConverter) {
    this.cellConverter = cellConverter;
  }

  /**
   * Initializes list of H3 cells
   * @return {@link Supplier} of H3 cells list
   */
  @Override
  public Supplier<List<Long>> supplier() {
    return ArrayList::new;
  }

  /**
   * Adds H3 cell to list
   * @return {@link BiConsumer} for adding H3 cells to list
   */
  @Override
  public BiConsumer<List<Long>, Long> accumulator() {
    return List::add;
  }

  /**
   * Merges 2 incomplete list of H3 cells
   * @return {@link BinaryOperator} that combines 2 incomplete list of H3 cells
   */
  @Override
  public BinaryOperator<List<Long>> combiner() {
    return (cells1, cells2) -> {
      cells1.addAll(cells2);
      return cells1;
    };
  }

  /**
   * Transforms list of H3 cells to JTS geometry
   * @return {@link Function} for transforming list of H3 cells to JTS geometry
   */
  @Override
  public Function<List<Long>, Geometry> finisher() {
    return cellConverter;
  }

  @Override
  public Set<Characteristics> characteristics() {
    return Set.of();
  }
}
