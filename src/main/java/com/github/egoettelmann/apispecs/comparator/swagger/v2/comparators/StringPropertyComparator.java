package com.github.egoettelmann.apispecs.comparator.swagger.v2.comparators;

import com.github.egoettelmann.apispecs.comparator.Comparator;
import com.github.egoettelmann.apispecs.comparator.ComparisonContext;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import com.github.egoettelmann.apispecs.comparator.ComparisonUtils;
import com.github.egoettelmann.apispecs.comparator.changes.BreakingChange;
import com.github.egoettelmann.apispecs.comparator.changes.ChangedModelProperty;
import io.swagger.models.properties.StringProperty;
import org.apache.commons.lang3.StringUtils;

public class StringPropertyComparator implements Comparator<StringProperty, StringProperty> {

    @Override
    public boolean accept(ComparisonContext<?, ?> context) {
        if (!context.source().isPresent() || !context.target().isPresent()) {
            return false;
        }
        return context.source().get() instanceof StringProperty
                && context.target().get() instanceof StringProperty;
    }

    @Override
    public ComparisonResult apply(ComparisonContext<StringProperty, StringProperty> context) {
        ComparisonResult result = new ComparisonResult();

        if (!context.source().isPresent() || !context.target().isPresent()) {
            return result;
        }

        StringProperty oldProperty = context.source().get();
        StringProperty newProperty = context.target().get();


        // Checking that the maximum did not decrease
        if (newProperty.getMaxLength() != null && newProperty.getMaxLength().compareTo(oldProperty.getMaxLength()) < 0) {
            BreakingChange breakingChange = ChangedModelProperty.of(
                    context.absolutePath(),
                    "maxLength",
                    oldProperty.getMaxLength().toString(),
                    newProperty.getMaxLength().toString()
            );
            result.add(breakingChange);
        }

        // Checking that the minimum dit not increase
        if (newProperty.getMinLength() != null && newProperty.getMinLength().compareTo(oldProperty.getMinLength()) > 0) {
            BreakingChange breakingChange = ChangedModelProperty.of(
                    context.absolutePath(),
                    "minLength",
                    oldProperty.getMinLength().toString(),
                    newProperty.getMinLength().toString()
            );
            result.add(breakingChange);
        }

        // Checking that the enumerations are compatible
        if (!ComparisonUtils.enumerationsAreCompatible(oldProperty.getEnum(), newProperty.getEnum())) {
            BreakingChange breakingChange = ChangedModelProperty.of(
                    context.absolutePath(),
                    "enum",
                    StringUtils.join(oldProperty.getEnum(), ","),
                    StringUtils.join(newProperty.getEnum(), ",")
            );
            result.add(breakingChange);
        }

        // Checking the pattern
        if (newProperty.getPattern() != null && !newProperty.getPattern().equals(oldProperty.getPattern())) {
            BreakingChange breakingChange = ChangedModelProperty.of(
                    context.absolutePath(),
                    "pattern",
                    oldProperty.getPattern(),
                    newProperty.getPattern()
            );
            result.add(breakingChange);
        }

        // TODO: do additional checks for all type specificities

        return result;
    }

}
