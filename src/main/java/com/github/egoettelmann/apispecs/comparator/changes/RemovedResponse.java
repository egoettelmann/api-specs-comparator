package com.github.egoettelmann.apispecs.comparator.changes;

import java.util.List;

public class RemovedResponse extends AbstractBreakingChange {

    private String code;

    private RemovedResponse(String message, List<String> path, String code) {
        super(message, path);
        this.code = code;
    }

    public static RemovedResponse of(List<String> path, String code) {
        String message = String.format("Removed response '%s'", code);
        return new RemovedResponse(message, path, code);
    }

    public String code() {
        return code;
    }
}
