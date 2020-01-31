package com.github.egoettelmann.apispecs.comparator;

import java.util.List;

public class ComparatorChain {

    private List<Comparator> comparators;

    public ComparatorChain(List<Comparator> comparators) {
        this.comparators = comparators;
    }

    public ComparisonResult apply(ComparisonContext<?, ?> context) {
        ComparisonResult result = new ComparisonResult();
        for (Comparator comparator : comparators) {
            if (comparator.accept(context)) {
                result.merge(comparator.apply(context, this));
            }
        }
        return result;
    }

}
