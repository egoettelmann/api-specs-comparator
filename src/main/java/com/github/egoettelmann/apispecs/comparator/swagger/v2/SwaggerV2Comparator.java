package com.github.egoettelmann.apispecs.comparator.swagger.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.egoettelmann.apispecs.comparator.ApiSpecificationsComparator;
import com.github.egoettelmann.apispecs.comparator.Comparator;
import com.github.egoettelmann.apispecs.comparator.ComparatorChain;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import com.github.egoettelmann.apispecs.comparator.swagger.v2.comparators.*;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SwaggerV2Comparator implements ApiSpecificationsComparator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerV2Comparator.class);

    private ObjectMapper objectMapper;

    private ComparatorChain comparatorChain;

    public SwaggerV2Comparator() {
        this(defaultComparators());
    }

    public SwaggerV2Comparator(List<Comparator> comparators) {
        this(Json.mapper(), comparators);
    }

    public SwaggerV2Comparator(ObjectMapper objectMapper, List<Comparator> comparators) {
        this.objectMapper = objectMapper;
        this.comparatorChain = new ComparatorChain(comparators);
    }

    public Optional<ComparisonResult> compare(String oldSpecifications, String newSpecifications) {
        // Building old specs
        Swagger oldSwaggerSpecs;
        try {
            oldSwaggerSpecs = objectMapper.readValue(oldSpecifications, Swagger.class);
        } catch (IOException e) {
            LOGGER.info("Could not deserialize existing API Specifications as Swagger V2 '{}'", oldSpecifications, e);
            return Optional.empty();
        }

        // Building new specs
        Swagger newSwaggerSpecs;
        try {
            newSwaggerSpecs = objectMapper.readValue(newSpecifications, Swagger.class);
        } catch (IOException e) {
            LOGGER.info("Could not deserialize new API Specifications as Swagger V2 '{}'", newSpecifications, e);
            return Optional.empty();
        }

        SwaggerV2Walker swaggerV2Walker = new SwaggerV2Walker(comparatorChain);
        return Optional.of(
                swaggerV2Walker.walk(oldSwaggerSpecs, newSwaggerSpecs)
        );
    }

    public static List<Comparator> defaultComparators() {
        return Arrays.asList(
                new SwaggerRootComparator(),
                new PathComparator(),
                new OperationComparator(),
                new ParameterComparator(),
                new ResponseComparator(),
                new ModelComparator(),
                new PropertyComparator()
        );
    }

}
