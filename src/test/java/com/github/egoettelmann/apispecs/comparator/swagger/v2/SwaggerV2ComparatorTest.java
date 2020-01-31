package com.github.egoettelmann.apispecs.comparator.swagger.v2;

import com.github.egoettelmann.apispecs.comparator.ComparisonResult;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class SwaggerV2ComparatorTest {

    private SwaggerV2Comparator comparator = new SwaggerV2Comparator();

    @Test
    public void testCompare_twoNewEndpoints_shouldHave_noBreakingChanges() throws IOException {
        InputStream isSpecsNew = getClass().getResourceAsStream("/specs-001-new.json");
        String specsNew = IOUtils.toString(isSpecsNew);
        InputStream isSpecsOld = getClass().getResourceAsStream("/specs-001-old.json");
        String specsOld = IOUtils.toString(isSpecsOld);
        Optional<ComparisonResult> result = comparator.compare(specsOld, specsNew);
        Assert.assertTrue("Comparison result should not be null", result.isPresent());
        Assert.assertEquals("There should be no breaking changes", 0, result.get().getBreakingChanges().size());
        Assert.assertEquals("There should be no non-breaking changes", 0, result.get().getNonBreakingChanges().size());
    }

    @Test
    public void testCompare_twoMissingEndpoints_shouldHave_twoBreakingChanges() throws IOException {
        InputStream isSpecsNew = getClass().getResourceAsStream("/specs-002-new.json");
        String specsNew = IOUtils.toString(isSpecsNew);
        InputStream isSpecsOld = getClass().getResourceAsStream("/specs-002-old.json");
        String specsOld = IOUtils.toString(isSpecsOld);
        Optional<ComparisonResult> result = comparator.compare(specsOld, specsNew);
        Assert.assertTrue("Comparison result should not be null", result.isPresent());
        Assert.assertEquals("There should be 2 breaking changes", 2, result.get().getBreakingChanges().size());
    }

    @Test
    public void testCompare_twoNewRequiredParameters_shouldHave_twoBreakingChanges() throws IOException {
        InputStream isSpecsNew = getClass().getResourceAsStream("/specs-003-new.json");
        String specsNew = IOUtils.toString(isSpecsNew);
        InputStream isSpecsOld = getClass().getResourceAsStream("/specs-003-old.json");
        String specsOld = IOUtils.toString(isSpecsOld);
        Optional<ComparisonResult> result = comparator.compare(specsOld, specsNew);
        Assert.assertTrue("Comparison result should not be null", result.isPresent());
        Assert.assertEquals("There should be 2 breaking changes", 2, result.get().getBreakingChanges().size());
    }

    @Test
    public void testCompare_removedRequestParameter_shouldHave_oneBreakingChange() throws IOException {
        InputStream isSpecsNew = getClass().getResourceAsStream("/specs-004-new.json");
        String specsNew = IOUtils.toString(isSpecsNew);
        InputStream isSpecsOld = getClass().getResourceAsStream("/specs-004-old.json");
        String specsOld = IOUtils.toString(isSpecsOld);
        Optional<ComparisonResult> result = comparator.compare(specsOld, specsNew);
        Assert.assertTrue("Comparison result should not be null", result.isPresent());
        Assert.assertEquals("There should be 1 breaking change", 1, result.get().getBreakingChanges().size());
    }

    @Test
    public void testCompare_removedTwoResponses_shouldHave_twoBreakingChanges() throws IOException {
        InputStream isSpecsNew = getClass().getResourceAsStream("/specs-005-new.json");
        String specsNew = IOUtils.toString(isSpecsNew);
        InputStream isSpecsOld = getClass().getResourceAsStream("/specs-005-old.json");
        String specsOld = IOUtils.toString(isSpecsOld);
        Optional<ComparisonResult> result = comparator.compare(specsOld, specsNew);
        Assert.assertTrue("Comparison result should not be null", result.isPresent());
        Assert.assertEquals("There should be 2 breaking changes", 2, result.get().getBreakingChanges().size());
    }

}
