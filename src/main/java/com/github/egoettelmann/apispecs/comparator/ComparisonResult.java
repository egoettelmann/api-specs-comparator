package com.github.egoettelmann.apispecs.comparator;

import com.github.egoettelmann.apispecs.comparator.changes.BreakingChange;
import com.github.egoettelmann.apispecs.comparator.changes.NonBreakingChange;

import java.util.ArrayList;
import java.util.List;

public class ComparisonResult {

    private List<BreakingChange> breakingChanges;

    private List<NonBreakingChange> nonBreakingChanges;

    public List<BreakingChange> getBreakingChanges() {
        if (breakingChanges == null) {
            breakingChanges = new ArrayList<>();
        }
        return breakingChanges;
    }

    public List<NonBreakingChange> getNonBreakingChanges() {
        if (nonBreakingChanges == null) {
            nonBreakingChanges = new ArrayList<>();
        }
        return nonBreakingChanges;
    }

    public void add(BreakingChange breakingChange) {
        getBreakingChanges().add(breakingChange);
    }

    public void add(NonBreakingChange nonBreakingChange) {
        getNonBreakingChanges().add(nonBreakingChange);
    }

    public void merge(ComparisonResult comparisonResult) {
        getBreakingChanges().addAll(comparisonResult.getBreakingChanges());
        getNonBreakingChanges().addAll(comparisonResult.getNonBreakingChanges());
    }

    public boolean isBreaking() {
        return !breakingChanges.isEmpty();
    }

}
