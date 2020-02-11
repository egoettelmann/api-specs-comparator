package com.github.egoettelmann.apispecs.comparator.swagger.v2;

import com.github.egoettelmann.apispecs.comparator.Comparator;
import com.github.egoettelmann.apispecs.comparator.ComparisonContext;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import com.github.egoettelmann.apispecs.comparator.changes.BreakingChange;
import com.github.egoettelmann.apispecs.comparator.changes.RemovedResponse;
import io.swagger.models.Response;

class ResponseComparator implements Comparator<Response, Response> {

    @Override
    public boolean accept(ComparisonContext<?, ?> context) {
        return context.canCompare(Response.class);
    }

    @Override
    public ComparisonResult apply(ComparisonContext<Response, Response> context) {
        ComparisonResult result = new ComparisonResult();

        if (!context.target().isPresent()) {
            BreakingChange breakingChange = RemovedResponse.of(context.absolutePath());
            result.add(breakingChange);
        }

        return result;
    }

}
