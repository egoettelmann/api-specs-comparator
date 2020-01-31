package com.github.egoettelmann.apispecs.comparator.changes;

import java.util.List;

public class AddedRequiredRequestParameter extends AbstractBreakingChange {

    private String name;

    private String type;

    private AddedRequiredRequestParameter(String message, List<String> path, String name, String type) {
        super(message, path);
        this.name = name;
        this.type = type;
    }

    public static AddedRequiredRequestParameter of(List<String> path, String name, String type) {
        String message = String.format("New required request parameter '%s' (%s)", name, type);
        return new AddedRequiredRequestParameter(message, path, name, type);
    }

    public String name() {
        return name;
    }

    public String type() {
        return type;
    }
}
