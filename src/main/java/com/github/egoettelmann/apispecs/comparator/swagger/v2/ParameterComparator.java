package com.github.egoettelmann.apispecs.comparator.swagger.v2;

import com.github.egoettelmann.apispecs.comparator.Comparator;
import com.github.egoettelmann.apispecs.comparator.ComparisonContext;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import com.github.egoettelmann.apispecs.comparator.changes.AddedRequiredRequestParameter;
import com.github.egoettelmann.apispecs.comparator.changes.BreakingChange;
import com.github.egoettelmann.apispecs.comparator.changes.ChangedRequestParameterType;
import com.github.egoettelmann.apispecs.comparator.changes.RemovedRequestParameter;
import io.swagger.models.parameters.AbstractSerializableParameter;
import io.swagger.models.parameters.Parameter;

class ParameterComparator implements Comparator<Parameter, Parameter> {

    @Override
    public boolean accept(ComparisonContext<?, ?> context) {
        return context.canCompare(Parameter.class);
    }

    @Override
    public ComparisonResult apply(ComparisonContext<Parameter, Parameter> context) {
        ComparisonResult result = new ComparisonResult();

        if (!context.source().isPresent() && !context.target().isPresent()) {
            // Nothing to compare
            return result;
        }

        // Checking that no parameter has been removed
        if (context.source().isPresent() && !context.target().isPresent()) {
            BreakingChange breakingChange = RemovedRequestParameter.of(
                    context.absolutePath(),
                    context.source().get().getName(),
                    context.source().get().getIn()
            );
            result.add(breakingChange);
            return result;
        }

        // Checking that no new required attribute has been added
        if (context.target().get().getRequired() && (!context.source().isPresent() || !context.source().get().getRequired())) {
            BreakingChange breakingChange = AddedRequiredRequestParameter.of(
                    context.absolutePath(),
                    context.target().get().getName(),
                    context.target().get().getIn()
            );
            result.add(breakingChange);
            return result;
        }
        Parameter source = context.source().get();
        Parameter target = context.target().get();

        if (!target.getClass().equals(source.getClass())) {
            // Should never happen, we should always compare params of same type
            BreakingChange breakingChange = ChangedRequestParameterType.of(
                    context.absolutePath(),
                    source.getClass().getName(),
                    target.getClass().getName()
            );
            result.add(breakingChange);
            return result;
        }

        // TODO: move this to dedicated classes

        // TODO: some types are backwards compatible
        //  - if a number becomes a string: ok
        //  - if an integer becomes a number: ok
        if (source instanceof AbstractSerializableParameter) {
            AbstractSerializableParameter oldTypedParam = (AbstractSerializableParameter) source;
            AbstractSerializableParameter newTypedParam = (AbstractSerializableParameter) target;
            if (!newTypedParam.getType().equals(oldTypedParam.getType())) {
                BreakingChange breakingChange = ChangedRequestParameterType.of(
                        context.absolutePath(),
                        oldTypedParam.getType(),
                        newTypedParam.getType()
                );
                result.add(breakingChange);
            }
        }

        return result;
    }

}
