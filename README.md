# h3-hull-generator

The h3-geometry-generator computes approximate geometries from csv or tif coordinates using Uber's H3 library and JTS.

## Adding to your project

Add the following dependency to your pom.xml

```xml
<dependency>
  <groupId>io.github.ci-cmg</groupId>
  <artifactId>h3-geometry-generator</artifactId>
  <version>4.0.0-SNAPSHOT</version>
</dependency>
```

## Runtime Requirements
* Java 17

## Building From Source
Maven 3.6.0+ is required.
```bash
mvn clean install
```

## Supported Input File Formats
* csv
* tif

## Usage

### Setup Geometry Generator

```java
import com.uber.h3core.H3Core;
import edu.colorado.cires.cmg.geometry_generator.GeometryGenerator;
import edu.colorado.cires.cmg.geometry_generator.collector.CellCollector;
import edu.colorado.cires.cmg.geometry_generator.collector.DouglasPeuckerCellCollector;
import edu.colorado.cires.cmg.geometry_generator.h3.H3JTSConverter;
import edu.colorado.cires.cmg.geometry_generator.reducer.DouglasPeuckerReducer;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

GeometryFactory geometryFactory = new GeometryFactory();

H3JTSConverter converter = H3JTSConverter.create(
  H3Core.newInstance(),
  8, // H3 resolution (0-15)
  geometryFactory
);

GeometryGenerator generator = new GeometryGenerator(
  converter,
  () -> new CellCollector(converter::cellsToMultiPolygon) // transforms H3 indices to a MultiPolygon
);

// a collector can also be configured to return a further simplified result
GeometryGenerator generator = new GeometryGenerator(
  converter,
  reducer,
  () -> new DouglasPeuckerCellCollector(
    converter::cellsToMultiPolygon,
    100, // maximum points in resulting geometry
    0.01, // Douglas-Peucker algorithm distance tolerance
    0.001 // interval to increase distance tolerance when point threshold is exceeded
  )
);
```

### Generate geometry from csv file
```java
import edu.colorado.cires.cmg.geometry_generator.reader.csv.CSVCoordinateReader;

CSVCoordinateReader csvCoordinateReader = new CSVCoordinateReader(record -> record.get(0), record -> record.get(1), ',');

try (
  InputStream inputStream = Files.newInputStream(...);
  Reader reader = new InputStreamReader(inputStream)
) {
  Geometry geometry = generator.generate(csvCoordinateReader.read(reader));
}
```

### Generate geometry from tif

```java
import edu.colorado.cires.cmg.geometry_generator.reader.csv.CSVCoordinateReader;
import edu.colorado.cires.cmg.geometry_generator.reader.tif.CloseableGeotiffReader;
import edu.colorado.cires.cmg.geometry_generator.reader.tif.GeoTiffCoordinateReader;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

try(
  ImageInputStream imageInputStream = ImageIO.createImageInputStream(new File(...));
  CloseableGeotiffReader reader = new CloseableGeotiffReader(imageInputStream)
){
  Geometry geometry = generator.generate(GeoTiffCoordinateReader.read(reader));
}
```
