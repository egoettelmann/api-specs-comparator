package com.github.egoettelmann.apispecs.comparator.changes;

import java.util.ArrayList;

public class DifferentBasePath extends AbstractBreakingChange {

    private String source;

    private String target;

    private DifferentBasePath(String message, String source, String target) {
        super(message, new ArrayList<>());
        this.source = source;
        this.target = target;
    }

    public static DifferentBasePath of(String source, String target) {
        String message = String.format("Different basePath '%s' > '%s'", source, target);
        return new DifferentBasePath(message, source, target);
    }

    public String source() {
        return source;
    }

    public String target() {
        return target;
    }

}
