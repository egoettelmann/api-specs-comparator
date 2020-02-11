package com.github.egoettelmann.apispecs.comparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComparisonContext<S, T> {

    private List<String> relativePath;

    private S source;

    private T target;

    private ComparisonContext<?, ?> parent;

    public ComparisonContext(S source, T target) {
        this.relativePath = new ArrayList<>();
        this.source = source;
        this.target = target;
    }

    public ComparisonContext<S, T> path(String path) {
        this.relativePath.add(path);
        return this;
    }

    public ComparisonContext<S, T> parent(ComparisonContext<?, ?> parent) {
        this.parent = parent;
        return this;
    }

    public List<String> path() {
        return relativePath;
    }

    public Optional<S> source() {
        return Optional.ofNullable(source);
    }

    public Optional<T> target() {
        return Optional.ofNullable(target);
    }

    public ComparisonContext<?, ?> parent() {
        return parent;
    }

    public List<String> absolutePath() {
        List<String> absolutePath = new ArrayList<>();
        if (parent != null) {
            absolutePath.addAll(parent.absolutePath());
        }
        absolutePath.addAll(relativePath);
        return absolutePath;
    }

    public <CS, CT> ComparisonContext<CS, CT> extend(CS childSource, CT childTarget) {
        ComparisonContext<CS, CT> newContext = new ComparisonContext<>(childSource, childTarget);
        newContext.parent(this);
        return newContext;
    }

    public ComparisonContext<?, ?> root() {
        if (parent != null) {
            return parent.root();
        }
        return this;
    }

    public boolean canCompare(Class clazz) {
        if (source().isPresent() && clazz.isInstance(source().get())) {
            return true;
        }
        return target().isPresent() && clazz.isInstance(target().get());
    }

}
