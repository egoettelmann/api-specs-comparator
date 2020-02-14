package com.github.egoettelmann.apispecs.comparator.swagger.v2.comparators;

import com.github.egoettelmann.apispecs.comparator.Comparator;
import com.github.egoettelmann.apispecs.comparator.ComparisonContext;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import com.github.egoettelmann.apispecs.comparator.changes.BreakingChange;
import com.github.egoettelmann.apispecs.comparator.changes.ChangedModelProperty;
import io.swagger.models.properties.AbstractNumericProperty;

public class NumericPropertyComparator implements Comparator<AbstractNumericProperty, AbstractNumericProperty> {

    @Override
    public boolean accept(ComparisonContext<?, ?> context) {
        if (!context.source().isPresent() || !context.target().isPresent()) {
            return false;
        }
        return context.source().get() instanceof AbstractNumericProperty
                && context.target().get() instanceof AbstractNumericProperty;
    }

    @Override
    public ComparisonResult apply(ComparisonContext<AbstractNumericProperty, AbstractNumericProperty> context) {
        ComparisonResult result = new ComparisonResult();

        if (!context.source().isPresent() || !context.target().isPresent()) {
            return result;
        }

        AbstractNumericProperty oldProperty = context.source().get();
        AbstractNumericProperty newProperty = context.target().get();

        // Checking that the maximum did not decrease
        if (newProperty.getMaximum() != null && newProperty.getMaximum().compareTo(oldProperty.getMaximum()) < 0) {
            BreakingChange breakingChange = ChangedModelProperty.of(
                    context.absolutePath(),
                    "maximum",
                    oldProperty.getMaximum().toString(),
                    newProperty.getMaximum().toString()
            );
            result.add(breakingChange);
        }
        // Checking that the minimum did not increase
        if (newProperty.getMinimum() != null && newProperty.getMinimum().compareTo(oldProperty.getMinimum()) > 0) {
            BreakingChange breakingChange = ChangedModelProperty.of(
                    context.absolutePath(),
                    "minimum",
                    oldProperty.getMinimum().toString(),
                    newProperty.getMinimum().toString()
            );
            result.add(breakingChange);
        }

        // TODO: do additional checks for all type specificities

        return result;
    }

}
