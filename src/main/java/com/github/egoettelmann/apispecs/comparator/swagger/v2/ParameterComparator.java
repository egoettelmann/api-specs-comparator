package com.github.egoettelmann.apispecs.comparator.swagger.v2;

import com.github.egoettelmann.apispecs.comparator.Comparator;
import com.github.egoettelmann.apispecs.comparator.ComparatorChain;
import com.github.egoettelmann.apispecs.comparator.ComparisonContext;
import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import com.github.egoettelmann.apispecs.comparator.changes.BreakingChange;
import com.github.egoettelmann.apispecs.comparator.changes.ChangedRequestParameterType;
import io.swagger.models.Model;
import io.swagger.models.parameters.AbstractSerializableParameter;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.RefParameter;

class ParameterComparator implements Comparator<Parameter, Parameter> {

    @Override
    public boolean accept(ComparisonContext<?, ?> context) {
        return (context.source() instanceof Parameter) && (context.target() instanceof Parameter);
    }

    @Override
    public ComparisonResult apply(ComparisonContext<Parameter, Parameter> context, ComparatorChain chain) {
        ComparisonResult result = new ComparisonResult();

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

        // TODO: some types are backwards compatible
        //  - if a number becomes a string: ok
        //  - if an integer becomes a number: ok
        if (context.target() instanceof AbstractSerializableParameter) {
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

        if (context.target() instanceof BodyParameter) {
            BodyParameter oldBodyParam = (BodyParameter) context.source();
            BodyParameter newBodyParam = (BodyParameter) context.target();
            Model oldSchema = oldBodyParam.getSchema();
            Model newSchema = newBodyParam.getSchema();
            ComparisonContext<Model, Model> modelContext = context.extend(oldSchema, newSchema);
            ComparisonResult modelResult = chain.apply(modelContext);
            result.merge(modelResult);
        }

        if (context.target() instanceof RefParameter) {
            RefParameter oldRefParameter = (RefParameter) context.source();
            RefParameter newRefParameter = (RefParameter) context.target();
            // TODO: get model from ref
        }

        return result;
    }

}
