package com.github.egoettelmann.apispecs.comparator.swagger.v2;

import com.github.egoettelmann.apispecs.comparator.Comparator;
import com.github.egoettelmann.apispecs.comparator.ComparisonContext;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import com.github.egoettelmann.apispecs.comparator.changes.RemovedEndpoint;
import io.swagger.models.Path;

class PathComparator implements Comparator<Path, Path> {

    @Override
    public boolean accept(ComparisonContext<?, ?> context) {
        return (context.source() instanceof Path) || (context.target() instanceof Path);
    }

    @Override
    public ComparisonResult apply(final ComparisonContext<Path, Path> context) {
        ComparisonResult result = new ComparisonResult();

        // Checking that target is not missing
        if (context.target() == null) {
            context.source().getOperationMap().keySet().stream()
                    .map(method -> RemovedEndpoint.of(context.absolutePath(), method.name()))
                    .forEach(result::add);
        }

        return result;
    }

}
