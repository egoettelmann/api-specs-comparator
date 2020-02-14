package com.github.egoettelmann.apispecs.comparator.swagger.v2;

import com.github.egoettelmann.apispecs.comparator.ComparatorChain;
import com.github.egoettelmann.apispecs.comparator.ComparisonContext;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import com.github.egoettelmann.apispecs.comparator.ComparisonUtils;
import io.swagger.models.*;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.RefParameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;

import java.util.Map;
import java.util.Optional;

class SwaggerV2Walker {

    private ComparatorChain comparatorChain;

    SwaggerV2Walker(ComparatorChain comparatorChain) {
        this.comparatorChain = comparatorChain;
    }

    ComparisonResult walk(final Swagger oldSpecifications, final Swagger newSpecifications) {
        ComparisonResult result = new ComparisonResult();

        // Building context
        ComparisonContext<Swagger, Swagger> context = new ComparisonContext<>(oldSpecifications, newSpecifications);

        // Comparing
        result.merge(comparatorChain.apply(context));

        // Walking root
        result.merge(walkRoot(context));

        return result;
    }

    private ComparisonResult walkRoot(ComparisonContext<Swagger, Swagger> context) {
        ComparisonResult result = new ComparisonResult();

        // Checking for null
        if (!context.source().isPresent() || !context.target().isPresent()) {
            return result;
        }
        Swagger source = context.source().get();
        Swagger target = context.target().get();

        // Looping over all existing paths
        for (Map.Entry<String, Path> oldPathEntry : source.getPaths().entrySet()) {
            // Building context
            String pathName = oldPathEntry.getKey();
            Path oldPath = oldPathEntry.getValue();
            Path newPath = target.getPaths().get(pathName);
            ComparisonContext<Path, Path> pathContext = context
                    .extend(oldPath, newPath)
                    .path(pathName);

            // Comparing
            result.merge(comparatorChain.apply(pathContext));

            // Walking path
            result.merge(walkPath(pathContext));
        }

        return result;
    }

    private ComparisonResult walkPath(ComparisonContext<Path, Path> context) {
        ComparisonResult result = new ComparisonResult();

        // Checking for null
        if (!context.source().isPresent() || !context.target().isPresent()) {
            return result;
        }
        Path source = context.source().get();
        Path target = context.target().get();

        // Looping over all existing operations
        for (Map.Entry<HttpMethod, Operation> oldOperationEntry : source.getOperationMap().entrySet()) {
            // Building context
            HttpMethod method = oldOperationEntry.getKey();
            Operation oldOperation = oldOperationEntry.getValue();
            Operation newOperation = target.getOperationMap().get(method);
            ComparisonContext<Operation, Operation> operationContext = context
                    .extend(oldOperation, newOperation)
                    .path(method.name());

            // Comparing
            result.merge(comparatorChain.apply(operationContext));

            // Walking operation
            result.merge(walkOperation(operationContext));
        }

        return result;
    }

    private ComparisonResult walkOperation(ComparisonContext<Operation, Operation> context) {
        ComparisonResult result = new ComparisonResult();

        // Checking for null
        if (!context.source().isPresent() || !context.target().isPresent()) {
            return result;
        }
        Operation source = context.source().get();
        Operation target = context.target().get();

        // Looping over all existing parameters
        for (Parameter oldParam : source.getParameters()) {
            // Building context
            Parameter newParam = target.getParameters().stream()
                    .filter(p -> p.getName().equals(oldParam.getName()))
                    .filter(p -> p.getIn().equals(oldParam.getIn()))
                    .findFirst()
                    .orElse(null);
            ComparisonContext<Parameter, Parameter> parameterContext = context
                    .extend(oldParam, newParam)
                    .path(oldParam.getName())
                    .path(oldParam.getIn());

            // Comparing
            result.merge(comparatorChain.apply(parameterContext));

            // Walking parameter
            result.merge(walkParameter(parameterContext));
        }

        // Looping over all new parameters
        for (Parameter newParam : target.getParameters()) {
            Optional<Parameter> oldParam = source.getParameters().stream()
                    .filter(p -> p.getName().equals(newParam.getName()))
                    .filter(p -> p.getIn().equals(newParam.getIn()))
                    .findAny();

            if (!oldParam.isPresent()) {
                ComparisonContext<Parameter, Parameter> parameterContext = context
                        .extend((Parameter) null, newParam)
                        .path(newParam.getName())
                        .path(newParam.getIn());

                // Comparing
                result.merge(comparatorChain.apply(parameterContext));

                // Walking parameter
                result.merge(walkParameter(parameterContext));
            }
        }

        // Looping over all responses
        for (Map.Entry<String, Response> oldResponseEntry : source.getResponses().entrySet()) {
            // Building context
            String oldCode = oldResponseEntry.getKey();
            Response oldResponse = oldResponseEntry.getValue();
            Response newResponse = null;
            if (target.getResponses() != null) {
                newResponse = target.getResponses().get(oldCode);
            }
            ComparisonContext<Response, Response> responseContext = context
                    .extend(oldResponse, newResponse)
                    .path(oldCode);

            // Comparing
            result.merge(comparatorChain.apply(responseContext));

            // Walking response
            result.merge(walkResponse(responseContext));
        }

        return result;
    }

    private ComparisonResult walkParameter(ComparisonContext<Parameter, Parameter> context) {
        ComparisonResult result = new ComparisonResult();

        // Checking for null
        if (!context.source().isPresent() || !context.target().isPresent()) {
            return result;
        }
        Parameter source = context.source().get();
        Parameter target = context.target().get();

        // If body parameter, extracting model
        if (source instanceof BodyParameter) {
            BodyParameter oldBodyParam = (BodyParameter) source;
            BodyParameter newBodyParam = (BodyParameter) target;
            Model oldSchema = oldBodyParam.getSchema();
            Model newSchema = newBodyParam.getSchema();
            ComparisonContext<Model, Model> modelContext = context
                    .extend(oldSchema, newSchema);

            // Comparing
            result.merge(comparatorChain.apply(modelContext));

            // Walking response
            result.merge(walkModel(modelContext));
        }

        // TODO: add a unit test for this case
        // If ref parameter, extracting model
        if (source instanceof RefParameter) {
            RefParameter oldRefParameter = (RefParameter) source;
            RefParameter newRefParameter = (RefParameter) target;
            Model oldSchema = ComparisonUtils.extractSourceDefinition(context, oldRefParameter.getSimpleRef());
            Model newSchema = ComparisonUtils.extractTargetDefinition(context, newRefParameter.getSimpleRef());
            ComparisonContext<Model, Model> modelContext = context
                    .extend(oldSchema, newSchema);

            // Comparing
            result.merge(comparatorChain.apply(modelContext));

            // Walking response
            result.merge(walkModel(modelContext));
        }

        return result;
    }

    private ComparisonResult walkResponse(ComparisonContext<Response, Response> context) {
        ComparisonResult result = new ComparisonResult();

        // Checking for null
        if (!context.source().isPresent() || !context.target().isPresent()) {
            return result;
        }
        Response source = context.source().get();
        Response target = context.target().get();

        // Building context
        Model oldSchema = source.getResponseSchema();
        Model newSchema = target.getResponseSchema();
        ComparisonContext<Model, Model> modelContext = context
                .extend(oldSchema, newSchema);

        // Comparing
        result.merge(comparatorChain.apply(modelContext));

        // Walking response
        result.merge(walkModel(modelContext));

        return result;
    }

    private ComparisonResult walkModel(ComparisonContext<Model, Model> context) {
        ComparisonResult result = new ComparisonResult();

        // Checking for null
        if (!context.source().isPresent() || !context.target().isPresent()) {
            return result;
        }
        Model source = context.source().get();
        Model target = context.target().get();

        // No source properties, comparing with referenced model
        if (source.getProperties() == null) {
            Model oldModel = ComparisonUtils.extractSourceDefinition(context, source.getReference());
            ComparisonContext<Model, Model> modelContext = context
                    .extend(oldModel, target);

            // Comparing
            result.merge(comparatorChain.apply(modelContext));

            // Walking response
            result.merge(walkModel(modelContext));

            return result;
        }

        // No target properties, comparing with referenced model
        if (target.getProperties() == null) {
            Model targetModel = ComparisonUtils.extractTargetDefinition(context, target.getReference());
            ComparisonContext<Model, Model> modelContext = context
                    .extend(source, targetModel);

            // Comparing
            result.merge(comparatorChain.apply(modelContext));

            // Walking response
            result.merge(walkModel(modelContext));

            return result;
        }

        // Looping over all properties
        for (Map.Entry<String, Property> propertyEntry : source.getProperties().entrySet()) {
            String oldName = propertyEntry.getKey();
            Property oldProperty = propertyEntry.getValue();
            Property newProperty = target.getProperties().get(oldName);
            ComparisonContext<Property, Property> propertyContext = context
                    .extend(oldProperty, newProperty)
                    .path(oldName);

            // Comparing
            result.merge(comparatorChain.apply(propertyContext));

            // Walking response
            result.merge(walkProperty(propertyContext));
        }

        return result;
    }

    private ComparisonResult walkProperty(ComparisonContext<Property, Property> context) {
        ComparisonResult result = new ComparisonResult();

        // Checking for null
        if (!context.source().isPresent() || !context.target().isPresent()) {
            return result;
        }
        Property source = context.source().get();
        Property target = context.target().get();

        if (source instanceof ArrayProperty && target instanceof ArrayProperty) {
            ArrayProperty oldArrayProperty = (ArrayProperty) source;
            ArrayProperty newArrayProperty = (ArrayProperty) target;
            Property oldProperty = oldArrayProperty.getItems();
            Property newProperty = newArrayProperty.getItems();
            ComparisonContext<Property, Property> propertyContext = context
                    .extend(oldProperty, newProperty)
                    .path("item");

            // Comparing
            result.merge(comparatorChain.apply(propertyContext));
        }

        return result;
    }

}
