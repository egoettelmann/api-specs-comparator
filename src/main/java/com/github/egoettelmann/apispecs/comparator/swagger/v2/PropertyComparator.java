package com.github.egoettelmann.apispecs.comparator.swagger.v2;

import com.github.egoettelmann.apispecs.comparator.Comparator;
import com.github.egoettelmann.apispecs.comparator.ComparisonContext;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import com.github.egoettelmann.apispecs.comparator.ComparisonUtils;
import com.github.egoettelmann.apispecs.comparator.changes.BreakingChange;
import com.github.egoettelmann.apispecs.comparator.changes.ChangedModelProperty;
import com.github.egoettelmann.apispecs.comparator.changes.RemovedModelProperty;
import io.swagger.models.properties.AbstractNumericProperty;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import org.apache.commons.lang3.StringUtils;

public class PropertyComparator implements Comparator<Property, Property> {

    @Override
    public boolean accept(ComparisonContext<?, ?> context) {
        return context.canCompare(Property.class);
    }

    @Override
    public ComparisonResult apply(ComparisonContext<Property, Property> context) {
        ComparisonResult result = new ComparisonResult();

        if (!context.source().isPresent()) {
            // Nothing to compare
            return result;
        }

        // Checking that no property has been removed
        if (!context.target().isPresent()) {
            BreakingChange breakingChange = RemovedModelProperty.of(
                    context.absolutePath()
            );
            result.add(breakingChange);
            return result;
        }

        Property oldProperty = context.source().get();
        Property newProperty = context.target().get();

        // Checking that the type has not changed
        if (!newProperty.getType().equals(oldProperty.getType())) {
            BreakingChange breakingChange = ChangedModelProperty.of(
                    context.absolutePath(),
                    "type",
                    oldProperty.getType(),
                    newProperty.getType()
            );
            result.add(breakingChange);
            return result;
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
            return result;
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
            return result;
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

        return result;
    }


    private ComparisonResult compareNumericProperties(ComparisonContext<Property, Property> context, AbstractNumericProperty oldNumericProperty, AbstractNumericProperty newNumericProperty) {
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

    private ComparisonResult compareStringProperties(ComparisonContext<Property, Property> context, StringProperty oldStringProperty, StringProperty newStringProperty) {
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

    private ComparisonResult compareArrayProperties(ComparisonContext<Property, Property> context, ArrayProperty oldArrayProperty, ArrayProperty newArrayProperty) {
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
