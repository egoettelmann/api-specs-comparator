API Specs Comparator
====================

[![Maven Central](https://img.shields.io/maven-central/v/com.github.egoettelmann/api-specs-comparator?style=flat-square&label=Maven%20Central)](https://search.maven.org/artifact/com.github.egoettelmann/api-specs-comparator)
![CircleCI build (master)](https://img.shields.io/circleci/build/github/egoettelmann/api-specs-comparator/master?label=Master&style=flat-square)
![CircleCI build (master)](https://img.shields.io/circleci/build/github/egoettelmann/api-specs-comparator/develop?label=Develop&style=flat-square)

This library can be used to compare API specifications in order to detect breaking changes.

The goal is to help preserving backwards compatibility when upgrading APIs.

Currently the library supports comparison of Swagger V2 definition files.

## Usage

Add to your pom the following Maven dependency:
```xml
<dependency>
  <groupId>com.github.egoettelmann</groupId>
  <artifactId>api-specs-comparator</artifactId>
  <version>0.1.0</version>
</dependency>
```

The comparator requires the content of the specifications to compare (as String).

It can be easily integrated in a unit test to break your CI/CD pipelines on breaking changes of your API.
But it can also be used to perform any required checks at runtime.

### Sample code

```java
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import com.github.egoettelmann.apispecs.comparator.swagger.v2.SwaggerV2Comparator;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class MainClass {

    public static void main(String[] args) throws IOException {
        // Loading the content of the specifications to compare
        InputStream isOldSpecs = MainClass.class.getResourceAsStream("/specifications-old.json");
        InputStream isNewSpecs = MainClass.class.getResourceAsStream("/specifications-new.json");
        String oldSpecifications = IOUtils.toString(isOldSpecs);
        String newSpecifications = IOUtils.toString(isNewSpecs);

        // Instantiating the Swagger V2 comparator
        SwaggerV2Comparator comparator = new SwaggerV2Comparator();
        Optional<ComparisonResult> result = comparator.compare(oldSpecifications, newSpecifications);
        
        // Nothing returned, the comparator was not able to process the contents
        if (!result.isPresent()) {
            System.err.println("Input files are not valid Swagger V2 specifications");
            return;
        }

        // The changes are breaking
        if (result.get().isBreaking()) {
            System.err.println("The new specifications are not backwards compatible with the previous ones");
            System.err.println("There are " + result.get().getBreakingChanges().size() + " breaking changes");
            return;
        }

        // The changes are backwards compatible
        System.out.println("The new specifications are backwards compatible with the previous ones");
    }

}
```

## Breaking changes

Following changes are considered as breaking:
 - Removing an endpoint
 - Removing a request parameter
 - Adding a required request parameter
 - Changing the type of a request parameter
 - Removing a response (based on the response HTTP status code)
 - Removing a response model's property
 - Changing the type of a response model's property

