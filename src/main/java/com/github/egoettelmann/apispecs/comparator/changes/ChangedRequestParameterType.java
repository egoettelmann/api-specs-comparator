package com.github.egoettelmann.apispecs.comparator.changes;

import java.util.List;

public class ChangedRequestParameterType extends AbstractBreakingChange {

    private String sourceType;

    private String targetType;

    private ChangedRequestParameterType(String message, List<String> path, String sourceType, String targetType) {
        super(message, path);
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

    public static ChangedRequestParameterType of(List<String> path, String sourceType, String targetType) {
        String message = String.format("The type of the request parameter changed from '%s' to '%s'", sourceType, targetType);
        return new ChangedRequestParameterType(message, path, sourceType, targetType);
    }

    public String sourceType() {
        return sourceType;
    }

    public String targetType() {
        return targetType;
    }

}
