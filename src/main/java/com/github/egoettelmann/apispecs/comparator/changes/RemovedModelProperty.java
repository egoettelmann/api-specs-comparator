package com.github.egoettelmann.apispecs.comparator.changes;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class RemovedModelProperty extends AbstractBreakingChange {

    private RemovedModelProperty(String message, List<String> path) {
        super(message, path);
    }

    public static RemovedModelProperty of(List<String> path) {
        String message = String.format("Removed model property '%s'", StringUtils.join(path, "/"));
        return new RemovedModelProperty(message, path);
    }
}
