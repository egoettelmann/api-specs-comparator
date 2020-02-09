package com.github.egoettelmann.apispecs.comparator;

import io.swagger.models.Model;
import io.swagger.models.Swagger;

import java.util.List;

public class ComparisonUtils {

    private static final String DEFINITIONS_PREFIX = "#/definitions/";

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
        return reference.replace(DEFINITIONS_PREFIX, "");
    }

    public static boolean enumerationsAreCompatible(List<String> oldEnumeration, List<String> newEnumeration) {
        if (newEnumeration != null && !newEnumeration.isEmpty()) {
            if (oldEnumeration == null || oldEnumeration.isEmpty()) {
                return false;
            }
            return newEnumeration.containsAll(oldEnumeration);
        }
        return true;
    }

}
