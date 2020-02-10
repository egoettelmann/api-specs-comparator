package com.github.egoettelmann.apispecs.comparator.swagger.v2;

import com.github.egoettelmann.apispecs.comparator.Comparator;
import com.github.egoettelmann.apispecs.comparator.ComparisonContext;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import com.github.egoettelmann.apispecs.comparator.changes.BreakingChange;
import com.github.egoettelmann.apispecs.comparator.changes.RemovedEndpoint;
import io.swagger.models.Operation;

class OperationComparator implements Comparator<Operation, Operation> {

    @Override
    public boolean accept(ComparisonContext<?, ?> context) {
        return (context.source() instanceof Operation) || (context.target() instanceof Operation);
    }

    @Override
    public ComparisonResult apply(ComparisonContext<Operation, Operation> context) {
        ComparisonResult result = new ComparisonResult();

        if (context.target() == null) {
            BreakingChange breakingChange = RemovedEndpoint.of(context.absolutePath());
            result.add(breakingChange);
        }

        return result;
    }

}
