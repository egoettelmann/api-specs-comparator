package com.github.egoettelmann.apispecs.comparator;

public interface Comparator<S, T> {

    boolean accept(ComparisonContext<?, ?> context);

    ComparisonResult apply(ComparisonContext<S, T> context, ComparatorChain chain);

}
