package com.github.egoettelmann.apispecs.comparator.changes;

import java.util.List;

public class ChangedModelProperty extends AbstractBreakingChange {

    private String type;

    private String source;

    private String target;

    private ChangedModelProperty(String message, List<String> path, String type, String source, String target) {
        super(message, path);
        this.type = type;
        this.source = source;
        this.target = target;
    }

    public static ChangedModelProperty of(List<String> path, String type, String source, String target) {
        String message = String.format("The model property changed (%s) from '%s' to '%s'", type, source, target);
        return new ChangedModelProperty(message, path, type, source, target);
    }

    public String type() {
        return type;
    }

    public String source() {
        return source;
    }

    public String target() {
        return target;
    }

}
