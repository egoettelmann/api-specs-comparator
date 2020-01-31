package com.github.egoettelmann.apispecs.comparator.changes;

import java.util.List;

public class RemovedModelProperty extends AbstractBreakingChange {

    private String name;

    private RemovedModelProperty(String message, List<String> path, String name) {
        super(message, path);
        this.name = name;
    }

    public static RemovedModelProperty of(List<String> path, String name) {
        String message = String.format("Removed model property '%s'", name);
        return new RemovedModelProperty(message, path, name);
    }

    public String name() {
        return name;
    }
}
