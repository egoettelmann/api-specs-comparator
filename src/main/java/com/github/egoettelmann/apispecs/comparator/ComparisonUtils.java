package com.github.egoettelmann.apispecs.comparator;

import io.swagger.models.Model;
import io.swagger.models.Swagger;

public class ComparisonUtils {

    private ComparisonUtils() {
        // Private constructor
    }

    public static Model extractSourceDefinition(ComparisonContext<?, ?> context, String reference) {
        ComparisonContext<?, ?> root = context.root();
        if (root.source() instanceof Swagger) {
            ComparisonContext<Swagger, ?> swaggerRoot = (ComparisonContext<Swagger, ?>) root;
            return swaggerRoot.source().getDefinitions().get(simpleReference(reference));
        }
        return null;
    }

    public static Model extractTargetDefinition(ComparisonContext<?, ?> context, String reference) {
        ComparisonContext<?, ?> root = context.root();
        if (root.target() instanceof Swagger) {
            ComparisonContext<?, Swagger> swaggerRoot = (ComparisonContext<?, Swagger>) root;
            return swaggerRoot.target().getDefinitions().get(simpleReference(reference));
        }
        return null;
    }

    public static String simpleReference(String reference) {
        if (reference == null) {
            return null;
        }
        return reference.replace("#/definitions/", "");
    }

}
