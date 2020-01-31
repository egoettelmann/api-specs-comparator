package com.github.egoettelmann.apispecs.comparator.changes;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBreakingChange implements BreakingChange {

    private String message;

    private List<String> path;

    protected AbstractBreakingChange(String message, List<String> path) {
        this.message = message;
        this.path = new ArrayList<>(path);
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public List<String> path() {
        return path;
    }

}
