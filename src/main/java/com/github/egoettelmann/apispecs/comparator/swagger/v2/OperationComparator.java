package com.github.egoettelmann.apispecs.comparator.swagger.v2;

import com.github.egoettelmann.apispecs.comparator.Comparator;
import com.github.egoettelmann.apispecs.comparator.ComparatorChain;
import com.github.egoettelmann.apispecs.comparator.ComparisonContext;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import com.github.egoettelmann.apispecs.comparator.changes.BreakingChange;
import com.github.egoettelmann.apispecs.comparator.changes.AddedRequiredRequestParameter;
import com.github.egoettelmann.apispecs.comparator.changes.RemovedRequestParameter;
import com.github.egoettelmann.apispecs.comparator.changes.RemovedResponse;
import io.swagger.models.Operation;
import io.swagger.models.Response;
import io.swagger.models.parameters.Parameter;

import java.util.Map;
import java.util.Optional;

class OperationComparator implements Comparator<Operation, Operation> {

    @Override
    public boolean accept(ComparisonContext<?, ?> context) {
        return (context.source() instanceof Operation) && (context.target() instanceof Operation);
    }

    @Override
    public ComparisonResult apply(ComparisonContext<Operation, Operation> context, ComparatorChain chain) {
        ComparisonResult result = new ComparisonResult();

        // Checking that a new required attribute has not been added
        for (Parameter newParam : context.target().getParameters()) {
            if (!newParam.getRequired()) {
                continue;
            }
            boolean exists = context.source().getParameters().stream()
                    .filter(Parameter::getRequired)
                    .anyMatch(p -> p.getName().equals(newParam.getName()));
            if (exists) {
                continue;
            }
            BreakingChange breakingChange = AddedRequiredRequestParameter.of(context.absolutePath(), newParam.getName(), newParam.getIn());
            result.add(breakingChange);
        }

        // Looping over all existing parameters
        for (Parameter oldParam : context.source().getParameters()) {
            Optional<Parameter> newParam = context.target().getParameters().stream()
                    .filter(p -> p.getName().equals(oldParam.getName()))
                    .filter(p -> p.getIn().equals(oldParam.getIn()))
                    .findFirst();
            if (!newParam.isPresent()) {
                BreakingChange breakingChange = RemovedRequestParameter.of(context.absolutePath(), oldParam.getName(), oldParam.getIn());
                result.add(breakingChange);
                continue;
            }

            ComparisonContext<Parameter, Parameter> parameterContext = context
                    .extend(oldParam, newParam.get())
                    .path(oldParam.getName())
                    .path(oldParam.getIn());
            ComparisonResult paramResult = chain.apply(parameterContext);
            result.merge(paramResult);
        }

        // Looping over all responses
        for (Map.Entry<String, Response> oldResponseEntry : context.source().getResponses().entrySet()) {
            String oldCode = oldResponseEntry.getKey();
            if (context.target().getResponses() == null || !context.target().getResponses().containsKey(oldCode)) {
                BreakingChange breakingChange = RemovedResponse.of(context.absolutePath(), oldCode);
                result.add(breakingChange);
                continue;
            }
            Response oldResponse = oldResponseEntry.getValue();
            Response newResponse = context.target().getResponses().get(oldCode);
            ComparisonContext<Response, Response> responseContext = context
                    .extend(oldResponse, newResponse)
                    .path(oldCode);
            ComparisonResult responseResult = chain.apply(responseContext);
            result.merge(responseResult);
        }

        return result;
    }

}
