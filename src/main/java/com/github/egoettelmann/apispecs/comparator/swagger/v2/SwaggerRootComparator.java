package com.github.egoettelmann.apispecs.comparator.swagger.v2;

import com.github.egoettelmann.apispecs.comparator.Comparator;
import com.github.egoettelmann.apispecs.comparator.ComparatorChain;
import com.github.egoettelmann.apispecs.comparator.ComparisonContext;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import com.github.egoettelmann.apispecs.comparator.changes.RemovedEndpoint;
import io.swagger.models.Path;
import io.swagger.models.Swagger;

import java.util.Map;

class SwaggerRootComparator implements Comparator<Swagger, Swagger> {

    @Override
    public boolean accept(ComparisonContext<?, ?> context) {
        return (context.source() instanceof Swagger) && (context.target() instanceof Swagger);
    }

    @Override
    public ComparisonResult apply(ComparisonContext<Swagger, Swagger> context, ComparatorChain chain) {
        ComparisonResult result = new ComparisonResult();

        // Looping over all existing paths
        for (Map.Entry<String, Path> oldPathEntry : context.source().getPaths().entrySet()) {
            String pathName = oldPathEntry.getKey();
            Path oldPath = oldPathEntry.getValue();

            // Checking that the path has not been removed
            if (!context.target().getPaths().containsKey(pathName)) {
                oldPath.getOperationMap().keySet().stream()
                        .map(method -> RemovedEndpoint.of(pathName, method.name()))
                        .forEach(result::add);
                continue;
            }

            // Performing path comparison
            Path newPath = context.target().getPaths().get(pathName);
            ComparisonContext<Path, Path> pathContext = context
                    .extend(oldPath, newPath)
                    .path(pathName);
            ComparisonResult pathResult = chain.apply(pathContext);
            result.merge(pathResult);
        }
        return result;
    }

}
