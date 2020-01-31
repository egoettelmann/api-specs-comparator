package com.github.egoettelmann.apispecs.comparator.changes;

import java.util.List;

public interface NonBreakingChange {

    String message();

    Level importance();

    List<String> path();

    enum Level {
        WARN,
        INFO,
        DEBUG,
        TRACE
    }

}
