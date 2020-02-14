package com.github.egoettelmann.apispecs.comparator.swagger.v2.comparators;

import com.github.egoettelmann.apispecs.comparator.Comparator;
import com.github.egoettelmann.apispecs.comparator.ComparisonContext;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import com.github.egoettelmann.apispecs.comparator.changes.BreakingChange;
import com.github.egoettelmann.apispecs.comparator.changes.ChangedModelProperty;
import com.github.egoettelmann.apispecs.comparator.changes.RemovedModelProperty;
import io.swagger.models.properties.Property;

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

        // TODO: do additional checks for all type specificities

        return result;
    }

}
