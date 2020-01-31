package com.github.egoettelmann.apispecs.comparator.changes;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractNonBreakingChange implements NonBreakingChange {

    private String message;

    private Level importance;

    private List<String> path;

    protected AbstractNonBreakingChange(String message, Level importance, String... path) {
        this.message = message;
        this.importance = importance;
        this.path = Arrays.asList(path);
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public Level importance() {
        return importance;
    }

    @Override
    public List<String> path() {
        return path;
    }

}
