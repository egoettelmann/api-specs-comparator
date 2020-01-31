package com.github.egoettelmann.apispecs.comparator.changes;

import java.util.List;

public class RemovedRequestParameter extends AbstractBreakingChange {

    private String name;

    private String type;

    private RemovedRequestParameter(String message, List<String> path, String name, String type) {
        super(message, path);
        this.name = name;
        this.type = type;
    }

    public static RemovedRequestParameter of(List<String> path, String name, String type) {
        String message = String.format("Removed request parameter '%s' (%s)", name, type);
        return new RemovedRequestParameter(message, path, name, type);
    }

    public String name() {
        return name;
    }

    public String type() {
        return type;
    }

}
