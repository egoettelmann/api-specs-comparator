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
        return (context.source() instanceof Parameter) || (context.target() instanceof Parameter);
    }

    @Override
    public ComparisonResult apply(ComparisonContext<Parameter, Parameter> context) {
        ComparisonResult result = new ComparisonResult();

        // Checking that no parameter has been removed
        if (context.source() != null && context.target() == null) {
            BreakingChange breakingChange = RemovedRequestParameter.of(
                    context.absolutePath(),
                    context.source().getName(),
                    context.source().getIn()
            );
            result.add(breakingChange);
            return result;
        }

        // Checking that no new required attribute has been added
        if (context.target().getRequired() && (context.source() == null || !context.source().getRequired())) {
            BreakingChange breakingChange = AddedRequiredRequestParameter.of(
                    context.absolutePath(),
                    context.target().getName(),
                    context.target().getIn()
            );
            result.add(breakingChange);
            return result;
        }

        if (!context.target().getClass().equals(context.source().getClass())) {
            // Should never happen, we should always compare params of same type
            BreakingChange breakingChange = ChangedRequestParameterType.of(
                    context.absolutePath(),
                    context.source().getClass().getName(),
                    context.target().getClass().getName()
            );
            result.add(breakingChange);
            return result;
        }

        // TODO: move this to dedicated classes

        // TODO: some types are backwards compatible
        //  - if a number becomes a string: ok
        //  - if an integer becomes a number: ok
        if (context.source() instanceof AbstractSerializableParameter) {
            AbstractSerializableParameter oldTypedParam = (AbstractSerializableParameter) context.source();
            AbstractSerializableParameter newTypedParam = (AbstractSerializableParameter) context.target();
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
