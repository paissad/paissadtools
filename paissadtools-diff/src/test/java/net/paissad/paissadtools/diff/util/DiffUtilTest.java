package net.paissad.paissadtools.diff.util;

import java.io.File;
import java.io.IOException;

import net.paissad.paissadtools.diff.TestConstants;

import org.junit.Assert;
import org.junit.Test;

public class DiffUtilTest {

    @Test(expected = IllegalArgumentException.class)
    public final void testIsText_Null() throws IOException {
        Assert.assertTrue(DiffUtil.isText(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testIsText_Directory() throws IOException {
        Assert.assertTrue(DiffUtil.isText(new File(".")));
    }

    @Test
    public final void testIsText_TextFile() throws IOException {
        Assert.assertTrue(DiffUtil.isText(TestConstants.LOREM_IPSUM_TEXT_1_FILE));
    }

    @Test
    public final void testIsText_BinaryFile() throws IOException {
        Assert.assertFalse(DiffUtil.isText(TestConstants.LOREM_IPSUM_ZIP_FILE));
    }

}
