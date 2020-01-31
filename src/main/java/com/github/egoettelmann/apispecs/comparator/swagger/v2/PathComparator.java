package com.github.egoettelmann.apispecs.comparator.swagger.v2;

import com.github.egoettelmann.apispecs.comparator.Comparator;
import com.github.egoettelmann.apispecs.comparator.ComparatorChain;
import com.github.egoettelmann.apispecs.comparator.ComparisonContext;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import com.github.egoettelmann.apispecs.comparator.changes.BreakingChange;
import com.github.egoettelmann.apispecs.comparator.changes.RemovedEndpoint;
import io.swagger.models.HttpMethod;
import io.swagger.models.Operation;
import io.swagger.models.Path;

import java.util.Map;

class PathComparator implements Comparator<Path, Path> {

    @Override
    public boolean accept(ComparisonContext<?, ?> context) {
        return (context.source() instanceof Path) && (context.target() instanceof Path);
    }

    @Override
    public ComparisonResult apply(final ComparisonContext<Path, Path> context, ComparatorChain chain) {
        ComparisonResult result = new ComparisonResult();

        // Looping over all existing operations
        for (Map.Entry<HttpMethod, Operation> oldOperationEntry : context.source().getOperationMap().entrySet()) {
            HttpMethod method = oldOperationEntry.getKey();

            // Checking that the operation has not been removed
            if (!context.target().getOperationMap().containsKey(method)) {
                BreakingChange breakingChange = RemovedEndpoint.of(context.absolutePath(), method.name());
                result.add(breakingChange);
                continue;
            }

            // Performing operation comparison
            Operation oldOperation = oldOperationEntry.getValue();
            Operation newOperation = context.target().getOperationMap().get(method);
            ComparisonContext<Operation, Operation> operationContext = context
                    .extend(oldOperation, newOperation)
                    .path(method.name());
            ComparisonResult operationResult = chain.apply(operationContext);
            result.merge(operationResult);
        }
        return result;
    }

}
