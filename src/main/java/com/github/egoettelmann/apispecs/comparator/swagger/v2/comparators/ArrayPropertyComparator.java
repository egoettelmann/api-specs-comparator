package com.github.egoettelmann.apispecs.comparator.swagger.v2.comparators;

import com.github.egoettelmann.apispecs.comparator.Comparator;
import com.github.egoettelmann.apispecs.comparator.ComparisonContext;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import com.github.egoettelmann.apispecs.comparator.changes.BreakingChange;
import com.github.egoettelmann.apispecs.comparator.changes.ChangedModelProperty;
import io.swagger.models.properties.ArrayProperty;

public class ArrayPropertyComparator implements Comparator<ArrayProperty, ArrayProperty> {

    @Override
    public boolean accept(ComparisonContext<?, ?> context) {
        if (!context.source().isPresent() || !context.target().isPresent()) {
            return false;
        }
        return context.source().get() instanceof ArrayProperty
                && context.target().get() instanceof ArrayProperty;
    }

    @Override
    public ComparisonResult apply(ComparisonContext<ArrayProperty, ArrayProperty> context) {
        ComparisonResult result = new ComparisonResult();

        if (!context.source().isPresent() || !context.target().isPresent()) {
            return result;
        }

        ArrayProperty oldProperty = context.source().get();
        ArrayProperty newProperty = context.target().get();

        // Checking that the max items did not decrease
        if (newProperty.getMaxItems() != null && newProperty.getMaxItems().compareTo(oldProperty.getMaxItems()) < 0) {
            BreakingChange breakingChange = ChangedModelProperty.of(
                    context.absolutePath(),
                    "maxItems",
                    String.valueOf(oldProperty.getMaxItems()),
                    String.valueOf(newProperty.getMaxItems())
            );
            result.add(breakingChange);
        }

        // Checking that the min items did not increase
        if (newProperty.getMinItems() != null && newProperty.getMinItems().compareTo(oldProperty.getMinItems()) > 0) {
            BreakingChange breakingChange = ChangedModelProperty.of(
                    context.absolutePath(),
                    "minItems",
                    String.valueOf(oldProperty.getMinItems()),
                    String.valueOf(newProperty.getMinItems())
            );
            result.add(breakingChange);
        }

        // Checking if the items must be unique but they were not before
        if (newProperty.getUniqueItems() != null
                && newProperty.getUniqueItems()
                && newProperty.getUniqueItems().equals(oldProperty.getUniqueItems())
        ) {
            BreakingChange breakingChange = ChangedModelProperty.of(
                    context.absolutePath(),
                    "uniqueItems",
                    String.valueOf(oldProperty.getUniqueItems()),
                    String.valueOf(newProperty.getUniqueItems())
            );
            result.add(breakingChange);
        }

        // TODO: do additional checks for all type specificities

        return result;
    }

}
