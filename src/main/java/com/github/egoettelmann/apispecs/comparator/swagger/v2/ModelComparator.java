package com.github.egoettelmann.apispecs.comparator.swagger.v2;

import com.github.egoettelmann.apispecs.comparator.Comparator;
import com.github.egoettelmann.apispecs.comparator.ComparisonContext;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import io.swagger.models.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelComparator implements Comparator<Model, Model> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelComparator.class);

    @Override
    public boolean accept(ComparisonContext<?, ?> context) {
        return (context.source() instanceof Model) || (context.target() instanceof Model);
    }

    @Override
    public ComparisonResult apply(ComparisonContext<Model, Model> context) {
        ComparisonResult result = new ComparisonResult();

        if (context.source() == null || context.target() == null) {
            LOGGER.warn("Response has no schema: '{}'", context.absolutePath());
            return result;
        }

        return result;
    }

}
