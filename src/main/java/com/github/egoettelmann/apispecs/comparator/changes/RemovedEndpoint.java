package com.github.egoettelmann.apispecs.comparator.changes;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RemovedEndpoint extends AbstractBreakingChange {

    private RemovedEndpoint(String message, String path, String method) {
        super(message, Arrays.asList(path, method));
    }

    private RemovedEndpoint(String message, List<String> path) {
        super(message, path);
    }

    public static RemovedEndpoint of(String path, String method) {
        String message = String.format("Removed endpoint '%s' (%s)", path, method);
        return new RemovedEndpoint(message, path, method);
    }

    public static RemovedEndpoint of(List<String> path, String method) {
        String message = String.format("Removed endpoint '%s' (%s)", StringUtils.join(path, "/"), method);
        List<String> newPath = new ArrayList<>(path);
        newPath.add(method);
        return new RemovedEndpoint(message, newPath);
    }

}
