package net.paissad.paissadtools.diff.impl.properties;

import static net.paissad.paissadtools.diff.TestConstants.PROPERTIES_1_FILE;
import static net.paissad.paissadtools.diff.TestConstants.PROPERTIES_2_FILE;
import static net.paissad.paissadtools.diff.TestConstants.PROPERTIES_3_FILE;
import static net.paissad.paissadtools.diff.TestConstants.PROPERTIES_4_FILE;

import java.io.File;

import net.paissad.paissadtools.diff.api.IDiffResult;
import net.paissad.paissadtools.diff.exception.DiffToolException;

import org.junit.Assert;
import org.junit.Test;

public class PropertiesDiffToolTest {

    @Test(expected = IllegalArgumentException.class)
    public final void testCompare_first_object_is_null() throws DiffToolException {
        new PropertiesDiffTool().compare(null, PROPERTIES_2_FILE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCompare_second_object_is_null() throws DiffToolException {
        new PropertiesDiffTool().compare(PROPERTIES_1_FILE, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCompare_first_object_is_not_a_file() throws DiffToolException {
        new PropertiesDiffTool().compare(new File("."), PROPERTIES_2_FILE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCompare_second_object_is_not_a_file() throws DiffToolException {
        new PropertiesDiffTool().compare(PROPERTIES_1_FILE, new File("."), null);
    }

    @Test
    public final void testCompare_same_properties_files() throws DiffToolException {
        final PropertiesDiffTool propertiesDiffTool = new PropertiesDiffTool();
        final IDiffResult result = propertiesDiffTool.compare(PROPERTIES_1_FILE, PROPERTIES_1_FILE, null);

        Assert.assertTrue(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_different_properties_files() throws DiffToolException {
        final PropertiesDiffTool propertiesDiffTool = new PropertiesDiffTool();
        final IDiffResult result = propertiesDiffTool.compare(PROPERTIES_2_FILE, PROPERTIES_3_FILE, null);

        Assert.assertFalse(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_null_exclusionrules() throws DiffToolException {
        final PropertiesDiffTool propertiesDiffTool = new PropertiesDiffTool();
        final PropertiesDiffOption option = new PropertiesDiffOption();
        option.setExclusionRules(null);
        final IDiffResult result = propertiesDiffTool.compare(PROPERTIES_2_FILE, PROPERTIES_3_FILE, option);

        Assert.assertFalse(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_both_files_matching_exclusionrules() throws DiffToolException {
        final PropertiesDiffTool propertiesDiffTool = new PropertiesDiffTool();
        final PropertiesDiffOption option = new PropertiesDiffOption();
        option.getExclusionRules().add(new PropertiesDiffExclusionRule() {

            @Override
            public boolean match(final File f) throws DiffToolException {
                return true; // match all !
            }
        });
        final IDiffResult result = propertiesDiffTool.compare(PROPERTIES_2_FILE, PROPERTIES_3_FILE, option);

        Assert.assertFalse(result.similar());
        Assert.assertTrue(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_first_file_matching_exclusionrules() throws DiffToolException {
        final PropertiesDiffTool propertiesDiffTool = new PropertiesDiffTool();
        final PropertiesDiffOption option = new PropertiesDiffOption();
        option.getExclusionRules().add(new PropertiesDiffExclusionRule() {

            @Override
            public boolean match(final File f) throws DiffToolException {
                return f.getName().equals(PROPERTIES_2_FILE.getName());
            }
        });
        final IDiffResult result = propertiesDiffTool.compare(PROPERTIES_2_FILE, PROPERTIES_3_FILE, option);

        Assert.assertFalse(result.similar());
        Assert.assertTrue(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_second_file_matching_exclusionrules() throws DiffToolException {
        final PropertiesDiffTool propertiesDiffTool = new PropertiesDiffTool();
        final PropertiesDiffOption option = new PropertiesDiffOption();
        option.getExclusionRules().add(new PropertiesDiffExclusionRule() {

            @Override
            public boolean match(final File f) throws DiffToolException {
                return f.getName().equals(PROPERTIES_3_FILE.getName());
            }
        });
        final IDiffResult result = propertiesDiffTool.compare(PROPERTIES_2_FILE, PROPERTIES_3_FILE, option);

        Assert.assertFalse(result.similar());
        Assert.assertTrue(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_no_file_matching_exclusionrules() throws DiffToolException {
        final PropertiesDiffTool propertiesDiffTool = new PropertiesDiffTool();
        final PropertiesDiffOption option = new PropertiesDiffOption();
        option.getExclusionRules().add(new PropertiesDiffExclusionRule() {

            @Override
            public boolean match(final File f) throws DiffToolException {
                return false; // match nothing !
            }
        });
        final IDiffResult result = propertiesDiffTool.compare(PROPERTIES_2_FILE, PROPERTIES_3_FILE, option);

        Assert.assertFalse(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_same_properties_files_with_verbosity() throws DiffToolException {
        final PropertiesDiffTool propertiesDiffTool = new PropertiesDiffTool();
        final PropertiesDiffOption option = new PropertiesDiffOption();
        option.setVerbose(true);
        final IDiffResult result = propertiesDiffTool.compare(PROPERTIES_1_FILE, PROPERTIES_1_FILE, option);

        Assert.assertTrue(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_different_properties_files_with_verbosity() throws DiffToolException {
        final PropertiesDiffTool propertiesDiffTool = new PropertiesDiffTool();
        final PropertiesDiffOption option = new PropertiesDiffOption();
        option.setVerbose(true);
        final IDiffResult result = propertiesDiffTool.compare(PROPERTIES_2_FILE, PROPERTIES_3_FILE, option);

        Assert.assertFalse(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_properties_files_having_same_keys_and_different_values_but_skippingValues()
            throws DiffToolException {

        final PropertiesDiffTool propertiesDiffTool = new PropertiesDiffTool();
        final PropertiesDiffOption option = new PropertiesDiffOption();
        option.setSkipValues(true);
        final IDiffResult result = propertiesDiffTool.compare(PROPERTIES_1_FILE, PROPERTIES_4_FILE, option);

        Assert.assertTrue(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_properties_files_having_same_keys_and_different_values_but_skippingValues_with_verbosity()
            throws DiffToolException {

        final PropertiesDiffTool propertiesDiffTool = new PropertiesDiffTool();
        final PropertiesDiffOption option = new PropertiesDiffOption();
        option.setVerbose(true);
        option.setSkipValues(true);
        final IDiffResult result = propertiesDiffTool.compare(PROPERTIES_1_FILE, PROPERTIES_4_FILE, option);

        Assert.assertTrue(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

}
