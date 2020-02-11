package com.github.egoettelmann.apispecs.comparator.swagger.v2.comparators;

import com.github.egoettelmann.apispecs.comparator.Comparator;
import com.github.egoettelmann.apispecs.comparator.ComparisonContext;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import com.github.egoettelmann.apispecs.comparator.changes.RemovedEndpoint;
import io.swagger.models.Path;

public class PathComparator implements Comparator<Path, Path> {

    @Override
    public boolean accept(ComparisonContext<?, ?> context) {
        return context.canCompare(Path.class);
    }

    @Override
    public ComparisonResult apply(final ComparisonContext<Path, Path> context) {
        ComparisonResult result = new ComparisonResult();

        if (!context.source().isPresent()) {
            // Nothing to compare
            return result;
        }

        // Checking that target is not missing
        if (!context.target().isPresent()) {
            context.source().get().getOperationMap().keySet().stream()
                    .map(method -> RemovedEndpoint.of(context.absolutePath(), method.name()))
                    .forEach(result::add);
        }

        return result;
    }

}
