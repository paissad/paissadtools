package net.paissad.paissadtools.compress.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;
import net.paissad.paissadtools.compress.TestConstants;
import net.paissad.paissadtools.compress.TestUtil;
import net.paissad.paissadtools.compress.api.CompressException;
import net.paissad.paissadtools.util.CommonUtils;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GzipToolTest {

    private static final String FILENAME_AA_GZ = "aa.gz";

    private static File         tmpDir;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        tmpDir = CommonUtils.createTempDir("_gziptooltest_", "_tmpdir_", TestUtil.getTempDir());
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        FileUtils.deleteQuietly(tmpDir);
    }

    @Test
    public final void testCompress_OK() throws CompressException {
        GzipTool gzipTool = new GzipTool();
        final File from = new File(TestConstants.DIR_SRC_TEST_RESOURCES, TestConstants.FILENAME_FILE);
        final File to = new File(tmpDir, from.getName() + ".gz");
        gzipTool.compress(from, to);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCompress_DIRECTORY() throws CompressException {
        GzipTool gzipTool = new GzipTool();
        // is a directory, should throw an exception.
        final File from = TestConstants.DIR_SRC_TEST_RESOURCES;
        final File to = new File(tmpDir, from.getName() + ".gz");
        gzipTool.compress(from, to);
    }

    @Test(expected = NullPointerException.class)
    public final void testCompress_NULL_FROM_FILE() throws CompressException {
        GzipTool gzipTool = new GzipTool();
        final File from = null;
        final File to = new File(tmpDir, "foo.gz");
        gzipTool.compress(from, to);
    }

    @Test(expected = NullPointerException.class)
    public final void testCompress_NULL_TO_FILE() throws CompressException {
        GzipTool gzipTool = new GzipTool();
        final File from = new File(TestConstants.DIR_SRC_TEST_RESOURCES, TestConstants.FILENAME_FILE);
        gzipTool.compress(from, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCompress_OUTPUTFILE_IS_A_DIR() throws CompressException {
        GzipTool gzipTool = new GzipTool();
        final File from = new File(TestConstants.DIR_SRC_TEST_RESOURCES, TestConstants.FILENAME_FILE);
        final File to = TestConstants.DIR_SRC_TEST_RESOURCES;
        gzipTool.compress(from, to);
    }

    @Test
    public final void testDecompress_OK() throws CompressException {
        final GzipTool gzipTool = new GzipTool();
        final File gzipFile = new File(TestConstants.DIR_SRC_TEST_RESOURCES, FILENAME_AA_GZ);
        final String destFilename = CommonUtils.removeExtension(gzipFile.getName());
        gzipTool.decompress(gzipFile, new File(tmpDir, destFilename));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testDecompress_FROM_IS_NOT_A_FILE() throws CompressException {
        final GzipTool gzipTool = new GzipTool();
        final File gzipFile = TestConstants.DIR_SRC_TEST_RESOURCES;
        gzipTool.decompress(gzipFile, new File(tmpDir, "foo"));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testDecompress_DESTINATION_IS_A_DIR() throws CompressException {
        final GzipTool gzipTool = new GzipTool();
        final File gzipFile = new File(TestConstants.DIR_SRC_TEST_RESOURCES, FILENAME_AA_GZ);
        gzipTool.decompress(gzipFile, TestConstants.DIR_SRC_TEST_RESOURCES);
    }

    @Test(expected = CompressException.class)
    public final void testAddResources() throws CompressException, IOException {
        FileUtils.copyFileToDirectory(new File(TestConstants.DIR_SRC_TEST_RESOURCES, FILENAME_AA_GZ), tmpDir);
        final File gzipFile = new File(tmpDir, FILENAME_AA_GZ);
        new GzipTool().addResources(gzipFile, null);
    }

    @Test
    public final void testList_OK() throws CompressException {
        final GzipTool gzipTool = new GzipTool();
        final List<String> result = gzipTool.list(new File(TestConstants.DIR_SRC_TEST_RESOURCES, FILENAME_AA_GZ));
        Assert.assertTrue(result.size() == 1);
        final String expected = "aa";
        final String actual = result.get(0);
        Assert.assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testList_IS_NOT_A_FILE() throws CompressException {
        final GzipTool gzipTool = new GzipTool();
        gzipTool.list(TestConstants.DIR_SRC_TEST_RESOURCES);
    }

    @Test
    public final void testGetAdapter() {
        // fake / dummy test !
        Assert.assertNull(new GzipTool().getAdapter(null));
    }

    @Test
    public final void testCanCompressDirectories() {
        Assert.assertFalse(new GzipTool().canCompressDirectories());
    }

    @Test
    public final void testCanAddResources() {
        Assert.assertFalse(new GzipTool().canAddResources());
    }

}
