package com.github.egoettelmann.apispecs.comparator.changes;

import java.util.List;

public interface BreakingChange {

    String message();

    List<String> path();

}
