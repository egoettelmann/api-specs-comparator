package com.github.egoettelmann.apispecs.comparator.swagger.v2;

import com.github.egoettelmann.apispecs.comparator.Comparator;
import com.github.egoettelmann.apispecs.comparator.ComparisonContext;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import com.github.egoettelmann.apispecs.comparator.changes.DifferentBasePath;
import com.github.egoettelmann.apispecs.comparator.changes.RemovedScheme;
import io.swagger.models.Scheme;
import io.swagger.models.Swagger;

class SwaggerRootComparator implements Comparator<Swagger, Swagger> {

    @Override
    public boolean accept(ComparisonContext<?, ?> context) {
        return (context.source() instanceof Swagger) && (context.target() instanceof Swagger);
    }

    @Override
    public ComparisonResult apply(ComparisonContext<Swagger, Swagger> context) {
        ComparisonResult result = new ComparisonResult();

        // Checking that no scheme was removed
        for (Scheme oldScheme : context.source().getSchemes()) {
            if (context.target().getSchemes().stream()
                    .noneMatch(newScheme -> newScheme.toValue().equals(oldScheme.toValue()))) {
                result.add(RemovedScheme.of(oldScheme.toValue()));
            }
        }

        if (!context.source().getBasePath().equals(context.target().getBasePath())) {
            // TODO: before generating this, maybe check all paths
            result.add(DifferentBasePath.of(context.source().getBasePath(), context.target().getBasePath()));
        }

        return result;
    }

}
