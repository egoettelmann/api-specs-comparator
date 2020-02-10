package com.github.egoettelmann.apispecs.comparator.changes;

import java.util.ArrayList;

public class RemovedScheme extends AbstractBreakingChange {

    private String scheme;

    private RemovedScheme(String message, String scheme) {
        super(message, new ArrayList<>());
        this.scheme = scheme;
    }

    public static RemovedScheme of(String scheme) {
        String message = String.format("Removed scheme '%s'", scheme);
        return new RemovedScheme(message, scheme);
    }

    public String scheme() {
        return scheme;
    }
}
