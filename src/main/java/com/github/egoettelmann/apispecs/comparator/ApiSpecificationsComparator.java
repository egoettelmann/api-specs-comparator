package com.github.egoettelmann.apispecs.comparator;

import java.util.Optional;

public interface ApiSpecificationsComparator {

    Optional<ComparisonResult> compare(final String oldSpecifications, final String newSpecifications);

}
