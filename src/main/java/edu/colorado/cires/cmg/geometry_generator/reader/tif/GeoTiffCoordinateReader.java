package edu.colorado.cires.cmg.geometry_generator.reader.tif;

import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.geometry.Position2D;
import org.locationtech.jts.geom.Coordinate;

/**
 * Reads coordinates from tif files
 * NOTE: it is assumed that tif files processed by this class have metadata pertaining to the geographic location of image contents
 */
public final class GeoTiffCoordinateReader {

  /**
   * Creates a {@link Stream<Coordinate>} from a {@link CloseableGeotiffReader}
   * @param reader {@link CloseableGeotiffReader} containing tif contents
   * @return {@link Stream<Coordinate>} containing coordinates from tif file
   * @throws IOException if tif file cannot be read
   */
  public static Stream<Coordinate> read(CloseableGeotiffReader reader) throws IOException {
    GridCoverage2D coverage2D = reader.read(null);

    return getTileStream(coverage2D.getRenderedImage())
      .flatMap(GeoTiffCoordinateReader::processRaster)
      .filter(GeoTiffCoordinateReader::filterPixel)
      .map(pixelValue -> pixelToCoordinate(pixelValue, coverage2D.getGridGeometry().getGridToCRS()));
  }

  /**
   * Transforms a tif pixel to a {@link Coordinate}
   * @param pixelValue {@link PixelValue} containing x / y pixel location
   * @param mathTransform {@link MathTransform} for converting x / y pixel locations to longitude / latitude
   * @return {@link Coordinate} representing the input pixel
   * @throws RuntimeException if {@link MathTransform} fails to convert pixel location to a coordinate
   */
  private static Coordinate pixelToCoordinate(PixelValue pixelValue, MathTransform mathTransform) {
    try {
      double[] coordinate = mathTransform.transform(new Position2D(pixelValue.x, pixelValue.y), null).getCoordinate();
      return new Coordinate(coordinate[0], coordinate[1]);
    } catch (TransformException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Filter determining which pixels are returned in this class' output {@link Stream<Coordinate>}
   * @param pixelValue {@link PixelValue} containing a numeric RGB value
   * @return whether this pixel will be included in this class' output {@link Stream<Coordinate>}
   */
  private static boolean filterPixel(PixelValue pixelValue) {
    double value = pixelValue.values[0];
    return  value != 255 && value != 0;
  }

  /**
   * Creates a {@link Stream<RasterValue>} for iterating through tif tiles
   * @param renderedImage {@link RenderedImage} containing tiles and tile metadata
   * @return {@link Stream<RasterValue>} of tif tiles
   */
  private static Stream<RasterValue> getTileStream(RenderedImage renderedImage) {
    return IntStream.range(0, renderedImage.getNumXTiles())
      .boxed()
      .flatMap(x ->
        IntStream.range(0, renderedImage.getNumYTiles())
          .boxed()
          .map(y -> new RasterValue(x, y, renderedImage.getTile(x, y)))
      );
  }

  /**
   * Creates a {@link Stream<PixelValue>} from a tif tile
   * @param rasterValue {@link RasterValue} representing tif tile
   * @return {@link Stream<PixelValue>} of pixels read from tif tile
   */
  private static Stream<PixelValue> processRaster(RasterValue rasterValue) {
    Raster raster = rasterValue.raster;
    int offsetX = rasterValue.x;
    int offsetY = rasterValue.y;

    return IntStream.range(0, raster.getWidth())
      .boxed()
      .flatMap(x ->
        IntStream.range(0, raster.getHeight())
          .boxed()
          .map(y -> new PixelValue(x + offsetX, y + offsetY, raster.getPixel(x + offsetX, y + offsetY, (double[]) null)))
      );
  }

  /**
   * Helper class representing a pixel's x / y location and its values across bands
   */
  private static class PixelValue {
    private final int x;
    private final int y;
    private final double[] values;

    /**
     * Creates a {@link PixelValue}
     * @param x pixel x location
     * @param y pixel y location
     * @param values RGB values for each band within tif image
     */
    private PixelValue(int x, int y, double[] values) {
      this.x = x;
      this.y = y;
      this.values = values;
    }
  }

  /**
   * Helper class representing a tile's x / y location and its corresponding raster image
   */
  private static class RasterValue {
    private final int x;
    private final int y;
    private final Raster raster;

    /**
     * Creates a {@link RasterValue}
     * @param x tile x location
     * @param y tile y location
     * @param raster tile's {@link Raster} image
     */
    private RasterValue(int x, int y, Raster raster) {
      this.x = x;
      this.y = y;
      this.raster = raster;
    }
  }
}
