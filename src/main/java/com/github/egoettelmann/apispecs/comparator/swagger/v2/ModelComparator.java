package com.github.egoettelmann.apispecs.comparator.swagger.v2;

import com.github.egoettelmann.apispecs.comparator.*;
import com.github.egoettelmann.apispecs.comparator.changes.BreakingChange;
import com.github.egoettelmann.apispecs.comparator.changes.ChangedModelProperty;
import com.github.egoettelmann.apispecs.comparator.changes.RemovedModelProperty;
import io.swagger.models.Model;
import io.swagger.models.properties.AbstractNumericProperty;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import org.apache.commons.lang3.StringUtils;

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
            Model oldModel = ComparisonUtils.extractSourceDefinition(context, context.source().getReference());
            ComparisonContext<Model, Model> modelContext = context.extend(oldModel, context.target());
            ComparisonResult modelResult = chain.apply(modelContext);
            result.merge(modelResult);
            return result;
        }

        if (context.target().getProperties() == null) {
            Model targetModel = ComparisonUtils.extractTargetDefinition(context, context.target().getReference());
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
                result.merge(
                        compareNumericProperties(
                                context,
                                oldNumericProperty,
                                newNumericProperty
                        )
                );
            }

            // Checking the type specific instances are compatible
            if (newProperty instanceof StringProperty) {
                StringProperty newStringProperty = (StringProperty) newProperty;
                StringProperty oldStringProperty = (StringProperty) oldProperty;
                result.merge(
                        compareStringProperties(
                                context,
                                oldStringProperty,
                                newStringProperty
                        )
                );
            }

            // Checking the type specific instances are compatible
            if (newProperty instanceof ArrayProperty) {
                ArrayProperty newArrayProperty = (ArrayProperty) newProperty;
                ArrayProperty oldArrayProperty = (ArrayProperty) oldProperty;
                result.merge(
                        compareArrayProperties(
                                context,
                                oldArrayProperty,
                                newArrayProperty
                        )
                );
            }

            // TODO: do additional checks for all type specificities

        }

        return result;
    }

    private ComparisonResult compareNumericProperties(ComparisonContext<Model, Model> context, AbstractNumericProperty oldNumericProperty, AbstractNumericProperty newNumericProperty) {
        ComparisonResult result = new ComparisonResult();

        // Checking that the maximum did not decrease
        if (newNumericProperty.getMaximum() != null && newNumericProperty.getMaximum().compareTo(oldNumericProperty.getMaximum()) < 0) {
            BreakingChange breakingChange = ChangedModelProperty.of(
                    context.absolutePath(),
                    "maximum",
                    oldNumericProperty.getMaximum().toString(),
                    newNumericProperty.getMaximum().toString()
            );
            result.add(breakingChange);
        }
        // Checking that the minimum did not increase
        if (newNumericProperty.getMinimum() != null && newNumericProperty.getMinimum().compareTo(oldNumericProperty.getMinimum()) > 0) {
            BreakingChange breakingChange = ChangedModelProperty.of(
                    context.absolutePath(),
                    "minimum",
                    oldNumericProperty.getMinimum().toString(),
                    newNumericProperty.getMinimum().toString()
            );
            result.add(breakingChange);
        }

        return result;
    }

    private ComparisonResult compareStringProperties(ComparisonContext<Model, Model> context, StringProperty oldStringProperty, StringProperty newStringProperty) {
        ComparisonResult result = new ComparisonResult();

        // Checking that the maximum did not decrease
        if (newStringProperty.getMaxLength() != null && newStringProperty.getMaxLength().compareTo(oldStringProperty.getMaxLength()) < 0) {
            BreakingChange breakingChange = ChangedModelProperty.of(
                    context.absolutePath(),
                    "maxLength",
                    oldStringProperty.getMaxLength().toString(),
                    newStringProperty.getMaxLength().toString()
            );
            result.add(breakingChange);
        }
        // Checking that the minimum dit not increase
        if (newStringProperty.getMinLength() != null && newStringProperty.getMinLength().compareTo(oldStringProperty.getMinLength()) > 0) {
            BreakingChange breakingChange = ChangedModelProperty.of(
                    context.absolutePath(),
                    "minLength",
                    oldStringProperty.getMinLength().toString(),
                    newStringProperty.getMinLength().toString()
            );
            result.add(breakingChange);
        }
        // Checking that the enumerations are compatible
        if (!ComparisonUtils.enumerationsAreCompatible(oldStringProperty.getEnum(), newStringProperty.getEnum())) {
            BreakingChange breakingChange = ChangedModelProperty.of(
                    context.absolutePath(),
                    "enum",
                    StringUtils.join(oldStringProperty.getEnum(), ","),
                    StringUtils.join(newStringProperty.getEnum(), ",")
            );
            result.add(breakingChange);
        }
        // Checking the pattern
        if (newStringProperty.getPattern() != null && !newStringProperty.getPattern().equals(oldStringProperty.getPattern())) {
            BreakingChange breakingChange = ChangedModelProperty.of(
                    context.absolutePath(),
                    "pattern",
                    oldStringProperty.getPattern(),
                    newStringProperty.getPattern()
            );
            result.add(breakingChange);
        }

        return result;
    }

    private ComparisonResult compareArrayProperties(ComparisonContext<Model, Model> context, ArrayProperty oldArrayProperty, ArrayProperty newArrayProperty) {
        ComparisonResult result = new ComparisonResult();
        // Checking that the max items did not decrease
        if (newArrayProperty.getMaxItems() != null && newArrayProperty.getMaxItems().compareTo(oldArrayProperty.getMaxItems()) < 0) {
            BreakingChange breakingChange = ChangedModelProperty.of(
                    context.absolutePath(),
                    "maxItems",
                    String.valueOf(oldArrayProperty.getMaxItems()),
                    String.valueOf(newArrayProperty.getMaxItems())
            );
            result.add(breakingChange);
        }

        // TODO: oldArrayProperty.getItems() recursive check properties
        return result;
    }

}
