package com.github.egoettelmann.apispecs.comparator.swagger.v2;

import com.github.egoettelmann.apispecs.comparator.Comparator;
import com.github.egoettelmann.apispecs.comparator.ComparatorChain;
import com.github.egoettelmann.apispecs.comparator.ComparisonContext;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import io.swagger.models.Model;
import io.swagger.models.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ResponseComparator implements Comparator<Response, Response> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseComparator.class);

    @Override
    public boolean accept(ComparisonContext<?, ?> context) {
        return (context.source() instanceof Response) && (context.target() instanceof Response);
    }

    @Override
    public ComparisonResult apply(ComparisonContext<Response, Response> context, ComparatorChain chain) {
        ComparisonResult result = new ComparisonResult();
        Model oldSchema = context.source().getResponseSchema();
        Model newSchema = context.target().getResponseSchema();
        if (newSchema == null || oldSchema == null) {
            LOGGER.warn("Response has no schema: '{}'", context.absolutePath());
            return result;
        }
        ComparisonContext<Model, Model> modelContext = context.extend(oldSchema, newSchema);
        ComparisonResult modelResult = chain.apply(modelContext);
        result.merge(modelResult);
        return result;
    }

}
