package edu.colorado.cires.cmg.geometry_generator.reader.tif;

import java.io.Closeable;
import javax.imageio.stream.ImageInputStream;
import org.geotools.api.data.DataSourceException;
import org.geotools.gce.geotiff.GeoTiffReader;

/**
 * {@link Closeable} wrapper for {@link GeoTiffReader} for use in try-with-resources blocks
 */
public class CloseableGeotiffReader extends GeoTiffReader implements Closeable {

  /**
   * Creates a {@link CloseableGeotiffReader}
   * @param input {@link ImageInputStream} containing tif file contents
   * @throws DataSourceException if {@link ImageInputStream} is invalid
   */
  public CloseableGeotiffReader(ImageInputStream input) throws DataSourceException {
    super(input);
  }

  /**
   * Cleans up {@link GeoTiffReader} resources, called automatically when {@link CloseableGeotiffReader} is used in try-with-resources blocks
   */
  @Override
  public void close() {
    dispose();
  }
}
