package com.github.egoettelmann.apispecs.comparator.swagger.v2;

import com.github.egoettelmann.apispecs.comparator.Comparator;
import com.github.egoettelmann.apispecs.comparator.ComparatorChain;
import com.github.egoettelmann.apispecs.comparator.ComparisonContext;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import com.github.egoettelmann.apispecs.comparator.changes.BreakingChange;
import com.github.egoettelmann.apispecs.comparator.changes.ChangedModelProperty;
import com.github.egoettelmann.apispecs.comparator.changes.RemovedModelProperty;
import io.swagger.models.Model;
import io.swagger.models.Swagger;
import io.swagger.models.properties.AbstractNumericProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;

import java.util.Map;

public class ModelComparator implements Comparator<Model, Model> {

    @Override
    public boolean accept(ComparisonContext<?, ?> context) {
        return (context.source() instanceof Model) && (context.target() instanceof Model);
    }

    @Override
    public ComparisonResult apply(ComparisonContext<Model, Model> context, ComparatorChain chain) {
        ComparisonResult result = new ComparisonResult();

        if (context.source().getProperties() == null) {
            Model oldModel = extractSourceDefinition(context, context.source().getReference());
            ComparisonContext<Model, Model> modelContext = context.extend(oldModel, context.target());
            ComparisonResult modelResult = chain.apply(modelContext);
            result.merge(modelResult);
            return result;
        }

        if (context.target().getProperties() == null) {
            Model targetModel = extractTargetDefinition(context, context.target().getReference());
            ComparisonContext<Model, Model> modelContext = context.extend(context.source(), targetModel);
            ComparisonResult modelResult = chain.apply(modelContext);
            result.merge(modelResult);
            return result;
        }

        for (Map.Entry<String, Property> propertyEntry : context.source().getProperties().entrySet()) {
            String oldName = propertyEntry.getKey();

            // Checking that no property has been removed
            if (!context.target().getProperties().containsKey(oldName)) {
                BreakingChange breakingChange = RemovedModelProperty.of(
                        context.absolutePath(),
                        oldName
                );
                result.add(breakingChange);
                continue;
            }

            Property oldProperty = propertyEntry.getValue();
            Property newProperty = context.target().getProperties().get(oldName);

            // Checking that the type has not changed
            if (!newProperty.getType().equals(oldProperty.getType())) {
                BreakingChange breakingChange = ChangedModelProperty.of(
                        context.absolutePath(),
                        "type",
                        oldProperty.getType(),
                        newProperty.getType()
                );
                result.add(breakingChange);
                continue;
            }

            // Checking that the format has not changed
            if (newProperty.getFormat() != null && !newProperty.getFormat().equals(oldProperty.getFormat())) {
                BreakingChange breakingChange = ChangedModelProperty.of(
                        context.absolutePath(),
                        "format",
                        oldProperty.getFormat(),
                        newProperty.getFormat()
                );
                result.add(breakingChange);
                continue;
            }


            // Checking that the class has not changed
            if (!newProperty.getClass().equals(oldProperty.getClass())) {
                BreakingChange breakingChange = ChangedModelProperty.of(
                        context.absolutePath(),
                        "class",
                        oldProperty.getClass().getName(),
                        newProperty.getClass().getName()
                );
                result.add(breakingChange);
                continue;
            }

            // Checking the type specific instances are compatible
            if (newProperty instanceof AbstractNumericProperty) {
                AbstractNumericProperty newNumericProperty = (AbstractNumericProperty) newProperty;
                AbstractNumericProperty oldNumericProperty = (AbstractNumericProperty) oldProperty;
                if (newNumericProperty.getMaximum() != null && newNumericProperty.getMaximum().compareTo(oldNumericProperty.getMaximum()) < 0) {
                    BreakingChange breakingChange = ChangedModelProperty.of(
                            context.absolutePath(),
                            "maximum",
                            oldNumericProperty.getMaximum().toString(),
                            newNumericProperty.getMaximum().toString()
                    );
                    result.add(breakingChange);
                }
                if (newNumericProperty.getMinimum() != null && newNumericProperty.getMinimum().compareTo(oldNumericProperty.getMinimum()) > 0) {
                    BreakingChange breakingChange = ChangedModelProperty.of(
                            context.absolutePath(),
                            "minimum",
                            oldNumericProperty.getMinimum().toString(),
                            newNumericProperty.getMinimum().toString()
                    );
                    result.add(breakingChange);
                }
            }

            // Checking the type specific instances are compatible
            if (newProperty instanceof StringProperty) {
                StringProperty newStringProperty = (StringProperty) newProperty;
                StringProperty oldStringProperty = (StringProperty) oldProperty;
                if (newStringProperty.getMaxLength() != null && newStringProperty.getMaxLength().compareTo(oldStringProperty.getMaxLength()) < 0) {
                    BreakingChange breakingChange = ChangedModelProperty.of(
                            context.absolutePath(),
                            "maxLength",
                            oldStringProperty.getMaxLength().toString(),
                            newStringProperty.getMaxLength().toString()
                    );
                    result.add(breakingChange);
                }
                if (newStringProperty.getMinLength() != null && newStringProperty.getMinLength().compareTo(oldStringProperty.getMinLength()) > 0) {
                    BreakingChange breakingChange = ChangedModelProperty.of(
                            context.absolutePath(),
                            "minLength",
                            oldStringProperty.getMinLength().toString(),
                            newStringProperty.getMinLength().toString()
                    );
                    result.add(breakingChange);
                }
            }
            // TODO: do additional checks for all type specificities

        }

        return result;
    }

    private Model extractSourceDefinition(ComparisonContext<?, ?> context, String reference) {
        ComparisonContext<?, ?> root = context.root();
        if (root.source() instanceof Swagger) {
            ComparisonContext<Swagger, ?> swaggerRoot = (ComparisonContext<Swagger, ?>) root;
            return swaggerRoot.source().getDefinitions().get(simpleReference(reference));
        }
        return null;
    }

    private Model extractTargetDefinition(ComparisonContext<?, ?> context, String reference) {
        ComparisonContext<?, ?> root = context.root();
        if (root.target() instanceof Swagger) {
            ComparisonContext<?, Swagger> swaggerRoot = (ComparisonContext<?, Swagger>) root;
            return swaggerRoot.target().getDefinitions().get(simpleReference(reference));
        }
        return null;
    }

    private String simpleReference(String reference) {
        if (reference == null) {
            return null;
        }
        return reference.replace("#/definitions/", "");
    }

}
