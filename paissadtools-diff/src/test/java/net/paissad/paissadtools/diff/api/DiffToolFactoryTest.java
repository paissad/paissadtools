package net.paissad.paissadtools.diff.api;

import java.util.List;

import net.paissad.paissadtools.diff.TestConstants;
import net.paissad.paissadtools.diff.exception.DiffToolException;
import net.paissad.paissadtools.diff.impl.file.FileDiffTool;

import org.junit.Assert;
import org.junit.Test;

public class DiffToolFactoryTest {

    @SuppressWarnings("rawtypes")
    @Test
    public final void testGetAvailablesDiffTools() {
        final DiffToolFactory factory = new DiffToolFactory();
        final List<IDiffTool> tools = factory.getAvailablesDiffTools();
        Assert.assertTrue(tools.size() >= 3);
    }

    @Test(expected = NullPointerException.class)
    public final void testGetDiffTool_null_classname() throws ClassNotFoundException {
        final DiffToolFactory factory = new DiffToolFactory();
        factory.getDiffTool(null);
    }

    @Test(expected = ClassNotFoundException.class)
    public final void testGetDiffTool_non_existent_implementation_class() throws ClassNotFoundException {
        final DiffToolFactory factory = new DiffToolFactory();
        factory.getDiffTool("net.paissad.paissadtools.diff.I_DONT_EXIST");
    }

    @Test
    public final void testGetDiffTool_factory_already_initialized() throws ClassNotFoundException, DiffToolException {
        final DiffToolFactory factory = new DiffToolFactory();
        factory.getAvailablesDiffTools(); // Initialization is performed when this method is invoked

        final FileDiffTool fileDiffTool = (FileDiffTool) factory
                .getDiffTool("net.paissad.paissadtools.diff.impl.file.FileDiffTool");

        IDiffResult result = fileDiffTool.compare(TestConstants.LOREM_IPSUM_TEXT_1_FILE,
                TestConstants.PROPERTIES_1_FILE, null);
        Assert.assertFalse(result.similar());
    }

    @Test
    public final void testGetDiffTool_factory_not_yet_initialized() throws ClassNotFoundException, DiffToolException {
        final FileDiffTool fileDiffTool = (FileDiffTool) new DiffToolFactory()
                .getDiffTool("net.paissad.paissadtools.diff.impl.file.FileDiffTool");

        IDiffResult result = fileDiffTool.compare(TestConstants.LOREM_IPSUM_TEXT_1_FILE,
                TestConstants.PROPERTIES_1_FILE, null);
        Assert.assertFalse(result.similar());
    }

}
