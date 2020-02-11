package com.github.egoettelmann.apispecs.comparator.swagger.v2;

import com.github.egoettelmann.apispecs.comparator.Comparator;
import com.github.egoettelmann.apispecs.comparator.ComparisonContext;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import com.github.egoettelmann.apispecs.comparator.changes.DifferentBasePath;
import com.github.egoettelmann.apispecs.comparator.changes.RemovedScheme;
import io.swagger.models.Scheme;
import io.swagger.models.Swagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SwaggerRootComparator implements Comparator<Swagger, Swagger> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerRootComparator.class);

    @Override
    public boolean accept(ComparisonContext<?, ?> context) {
        return context.canCompare(Swagger.class);
    }

    @Override
    public ComparisonResult apply(ComparisonContext<Swagger, Swagger> context) {
        ComparisonResult result = new ComparisonResult();

        if (!context.source().isPresent() || !context.target().isPresent()) {
            LOGGER.warn("Swagger has no schemes");
            return result;
        }
        Swagger source = context.source().get();
        Swagger target = context.target().get();

        // Checking that no scheme was removed
        for (Scheme oldScheme : source.getSchemes()) {
            if (target.getSchemes().stream()
                    .noneMatch(newScheme -> newScheme.toValue().equals(oldScheme.toValue()))
            ) {
                result.add(RemovedScheme.of(oldScheme.toValue()));
            }
        }

        if (!source.getBasePath().equals(target.getBasePath())) {
            // TODO: before generating this, maybe check all paths
            result.add(DifferentBasePath.of(source.getBasePath(), target.getBasePath()));
        }

        return result;
    }

}
