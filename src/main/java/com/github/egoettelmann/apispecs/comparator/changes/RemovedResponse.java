package com.github.egoettelmann.apispecs.comparator.changes;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class RemovedResponse extends AbstractBreakingChange {

    private RemovedResponse(String message, List<String> path) {
        super(message, path);
    }

    public static RemovedResponse of(List<String> path) {
        String message = String.format("Removed response '%s'", StringUtils.join(path, "/"));
        return new RemovedResponse(message, path);
    }

}
