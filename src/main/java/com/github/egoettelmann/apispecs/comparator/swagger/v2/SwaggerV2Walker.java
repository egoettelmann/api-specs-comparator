package com.github.egoettelmann.apispecs.comparator.swagger.v2;

import com.github.egoettelmann.apispecs.comparator.ComparatorChain;
import com.github.egoettelmann.apispecs.comparator.ComparisonContext;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import com.github.egoettelmann.apispecs.comparator.ComparisonUtils;
import io.swagger.models.*;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.RefParameter;
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
        if (context.source() == null || context.target() == null) {
            return result;
        }

        // Looping over all existing paths
        for (Map.Entry<String, Path> oldPathEntry : context.source().getPaths().entrySet()) {
            // Building context
            String pathName = oldPathEntry.getKey();
            Path oldPath = oldPathEntry.getValue();
            Path newPath = context.target().getPaths().get(pathName);
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
        if (context.source() == null || context.target() == null) {
            return result;
        }

        // Looping over all existing operations
        for (Map.Entry<HttpMethod, Operation> oldOperationEntry : context.source().getOperationMap().entrySet()) {
            // Building context
            HttpMethod method = oldOperationEntry.getKey();
            Operation oldOperation = oldOperationEntry.getValue();
            Operation newOperation = context.target().getOperationMap().get(method);
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

    private ComparisonResult walkOperation(ComparisonContext<Operation, Operation> operationContext) {
        ComparisonResult result = new ComparisonResult();

        // Checking for null
        if (operationContext.source() == null || operationContext.target() == null) {
            return result;
        }

        // Looping over all existing parameters
        for (Parameter oldParam : operationContext.source().getParameters()) {
            // Building context
            Parameter newParam = operationContext.target().getParameters().stream()
                    .filter(p -> p.getName().equals(oldParam.getName()))
                    .filter(p -> p.getIn().equals(oldParam.getIn()))
                    .findFirst()
                    .orElse(null);
            ComparisonContext<Parameter, Parameter> parameterContext = operationContext
                    .extend(oldParam, newParam)
                    .path(oldParam.getName())
                    .path(oldParam.getIn());

            // Comparing
            result.merge(comparatorChain.apply(parameterContext));

            // Walking parameter
            result.merge(walkParameter(parameterContext));
        }

        // Looping over all new parameters
        for (Parameter newParam : operationContext.target().getParameters()) {
            Optional<Parameter> oldParam = operationContext.source().getParameters().stream()
                    .filter(p -> p.getName().equals(newParam.getName()))
                    .filter(p -> p.getIn().equals(newParam.getIn()))
                    .findAny();

            if (!oldParam.isPresent()) {
                ComparisonContext<Parameter, Parameter> parameterContext = operationContext
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
        for (Map.Entry<String, Response> oldResponseEntry : operationContext.source().getResponses().entrySet()) {
            // Building context
            String oldCode = oldResponseEntry.getKey();
            Response oldResponse = oldResponseEntry.getValue();
            Response newResponse = null;
            if (operationContext.target().getResponses() != null) {
                newResponse = operationContext.target().getResponses().get(oldCode);
            }
            ComparisonContext<Response, Response> responseContext = operationContext
                    .extend(oldResponse, newResponse)
                    .path(oldCode);

            // Comparing
            result.merge(comparatorChain.apply(responseContext));

            // Walking response
            result.merge(walkResponse(responseContext));
        }

        return result;
    }

    private ComparisonResult walkParameter(ComparisonContext<Parameter, Parameter> parameterContext) {
        ComparisonResult result = new ComparisonResult();

        // Checking for null
        if (parameterContext.source() == null || parameterContext.target() == null) {
            return result;
        }

        // If body parameter, extracting model
        if (parameterContext.source() instanceof BodyParameter) {
            BodyParameter oldBodyParam = (BodyParameter) parameterContext.source();
            BodyParameter newBodyParam = (BodyParameter) parameterContext.target();
            Model oldSchema = oldBodyParam.getSchema();
            Model newSchema = newBodyParam.getSchema();
            ComparisonContext<Model, Model> modelContext = parameterContext
                    .extend(oldSchema, newSchema);

            // Comparing
            result.merge(comparatorChain.apply(modelContext));

            // Walking response
            result.merge(walkModel(modelContext));
        }

        // TODO: add a unit test for this case
        // If ref parameter, extracting model
        if (parameterContext.source() instanceof RefParameter) {
            RefParameter oldRefParameter = (RefParameter) parameterContext.source();
            RefParameter newRefParameter = (RefParameter) parameterContext.target();
            Model oldSchema = ComparisonUtils.extractSourceDefinition(parameterContext, oldRefParameter.getSimpleRef());
            Model newSchema = ComparisonUtils.extractTargetDefinition(parameterContext, newRefParameter.getSimpleRef());
            ComparisonContext<Model, Model> modelContext = parameterContext
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
        if (context.source() == null || context.target() == null) {
            return result;
        }

        // Building context
        Model oldSchema = context.source().getResponseSchema();
        Model newSchema = context.target().getResponseSchema();
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
        if (context.source() == null || context.target() == null) {
            return result;
        }

        // No source properties, comparing with referenced model
        if (context.source().getProperties() == null) {
            Model oldModel = ComparisonUtils.extractSourceDefinition(context, context.source().getReference());
            ComparisonContext<Model, Model> modelContext = context
                    .extend(oldModel, context.target());

            // Comparing
            result.merge(comparatorChain.apply(modelContext));

            // Walking response
            result.merge(walkModel(modelContext));

            return result;
        }

        // No target properties, comparing with referenced model
        if (context.target().getProperties() == null) {
            Model targetModel = ComparisonUtils.extractTargetDefinition(context, context.target().getReference());
            ComparisonContext<Model, Model> modelContext = context
                    .extend(context.source(), targetModel);

            // Comparing
            result.merge(comparatorChain.apply(modelContext));

            // Walking response
            result.merge(walkModel(modelContext));

            return result;
        }

        // Looping over all properties
        for (Map.Entry<String, Property> propertyEntry : context.source().getProperties().entrySet()) {
            String oldName = propertyEntry.getKey();
            Property oldProperty = propertyEntry.getValue();
            Property newProperty = context.target().getProperties().get(oldName);
            ComparisonContext<Property, Property> propertyContext = context
                    .extend(oldProperty, newProperty)
                    .path(oldName);

            // Comparing
            result.merge(comparatorChain.apply(propertyContext));
        }

        return result;
    }

}
