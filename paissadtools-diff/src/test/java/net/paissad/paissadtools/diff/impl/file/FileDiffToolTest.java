package net.paissad.paissadtools.diff.impl.file;

import static net.paissad.paissadtools.diff.TestConstants.LOREM_IPSUM_TEXT_2_FILE;
import static net.paissad.paissadtools.diff.TestConstants.LOREM_IPSUM_TEXT_1_FILE;
import static net.paissad.paissadtools.diff.TestConstants.LOREM_IPSUM_ZIP_FILE;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.UUID;

import net.paissad.paissadtools.diff.TestConstants;
import net.paissad.paissadtools.diff.api.IDiffResult;
import net.paissad.paissadtools.diff.exception.DiffToolException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public class FileDiffToolTest {

    @Test
    public final void testCompare_compare_same_text_files() throws DiffToolException {
        final FileDiffTool fileDiffTool = new FileDiffTool();
        final IDiffResult result = fileDiffTool.compare(LOREM_IPSUM_TEXT_1_FILE, LOREM_IPSUM_TEXT_1_FILE, null);
        Assert.assertTrue(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_compare_same_binary_files() throws DiffToolException {
        final FileDiffTool fileDiffTool = new FileDiffTool();
        final IDiffResult result = fileDiffTool.compare(LOREM_IPSUM_ZIP_FILE, LOREM_IPSUM_ZIP_FILE, null);
        Assert.assertTrue(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_compare_different_files() throws DiffToolException {
        final FileDiffTool fileDiffTool = new FileDiffTool();
        final IDiffResult result = fileDiffTool.compare(LOREM_IPSUM_TEXT_1_FILE, LOREM_IPSUM_TEXT_2_FILE, null);
        Assert.assertFalse(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_compare_same_directories() throws DiffToolException, IOException {

        final File tmpDir = new File(FileUtils.getTempDirectory(), UUID.randomUUID().toString());

        try {
            FileUtils.copyDirectory(TestConstants.TEST_DIR, tmpDir);
            final FileDiffTool fileDiffTool = new FileDiffTool();

            final IDiffResult result = fileDiffTool.compare(TestConstants.TEST_DIR, tmpDir, null);
            Assert.assertTrue(result.similar());
            Assert.assertFalse(result.isExcluded());
            Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());

        } finally {
            FileUtils.deleteQuietly(tmpDir);
        }
    }

    @Test
    public final void testCompare_compare_different_directories() throws DiffToolException, IOException {

        final File tmpDir = new File(FileUtils.getTempDirectory(), UUID.randomUUID().toString());

        try {
            FileUtils.copyDirectory(TestConstants.TEST_DIR, tmpDir, new FileFilter() {

                @Override
                public boolean accept(final File f) {
                    return f.getName().endsWith(".java");
                }
            });
            FileUtils.touch(new File(tmpDir, "foobar.txt"));
            final FileDiffTool fileDiffTool = new FileDiffTool();

            final IDiffResult result = fileDiffTool.compare(TestConstants.TEST_DIR, tmpDir, null);
            Assert.assertFalse(result.similar());
            Assert.assertFalse(result.isExcluded());
            Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());

        } finally {
            FileUtils.deleteQuietly(tmpDir);
        }
    }

    @Test
    public final void testCompare_compare_directory_and_file() throws DiffToolException {
        final FileDiffTool fileDiffTool = new FileDiffTool();
        final IDiffResult result = fileDiffTool.compare(TestConstants.TEST_DIR, LOREM_IPSUM_TEXT_1_FILE, null);
        Assert.assertFalse(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_compare_file_and_directory() throws DiffToolException {
        final FileDiffTool fileDiffTool = new FileDiffTool();
        final IDiffResult result = fileDiffTool.compare(LOREM_IPSUM_TEXT_1_FILE, TestConstants.TEST_DIR, null);
        Assert.assertFalse(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_compare_directories_with_non_null_option() throws DiffToolException {
        final FileDiffTool fileDiffTool = new FileDiffTool();
        final IDiffResult result = fileDiffTool.compare(TestConstants.TEST_DIR, TestConstants.TEST_DIR,
                new FileDiffOption());
        Assert.assertTrue(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_compare_directories_with_verbose() throws DiffToolException {
        final FileDiffTool fileDiffTool = new FileDiffTool();
        final FileDiffOption option = new FileDiffOption();
        option.setVerbose(true);
        final IDiffResult result = fileDiffTool.compare(TestConstants.TEST_DIR, TestConstants.TEST_DIR, option);
        Assert.assertTrue(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_compare_directories_with_file_exlusions() throws DiffToolException {
        final FileDiffTool fileDiffTool = new FileDiffTool();
        final FileDiffOption option = new FileDiffOption();
        final FileDiffExclusionRule exclusionRule = new FileDiffExclusionRule() {

            @Override
            public boolean match(final File f) throws DiffToolException {
                return f.getName().endsWith(".java"); // exclude java files !
            }
        };
        option.getExclusionRules().add(exclusionRule);
        final IDiffResult result = fileDiffTool.compare(TestConstants.TEST_DIR, TestConstants.TEST_DIR, option);
        Assert.assertTrue(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_compare_directories_with_dir_exlusions() throws DiffToolException {
        final FileDiffTool fileDiffTool = new FileDiffTool();
        final FileDiffOption option = new FileDiffOption();
        final FileDiffExclusionRule exclusionRule = new FileDiffExclusionRule() {

            @Override
            public boolean match(final File f) throws DiffToolException {
                return f.isDirectory() && f.getName().equals("exception"); // exclude 'exception' directory !
            }
        };
        option.getExclusionRules().add(exclusionRule);
        final IDiffResult result = fileDiffTool.compare(TestConstants.TEST_DIR, TestConstants.TEST_DIR, option);
        Assert.assertTrue(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

}
