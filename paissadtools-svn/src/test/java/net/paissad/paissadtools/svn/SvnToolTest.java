package net.paissad.paissadtools.svn;

import java.io.File;
import java.io.IOException;

import net.paissad.paissadtools.svn.exception.SVNToolException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public class SvnToolTest {

    @Test
    public final void testCd() {
        final File sysTempDir = FileUtils.getTempDirectory();
        final SvnTool svnTool = new SvnTool();
        svnTool.cd(sysTempDir);
        final String actual = svnTool.getCwd();
        final String expected = sysTempDir.getAbsolutePath();
        Assert.assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCd_Must_Not_Be_Null() {
        new SvnTool().cd(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCd_Must_Be_Directory() throws IOException {
        final File tempfile = File.createTempFile("svntool_", ".temp");
        try {
            new SvnTool().cd(tempfile);
        } finally {
            FileUtils.deleteQuietly(tempfile);
        }
    }

    @Test
    public final void testGetCwd() {
        final String actual = new SvnTool().getCwd();
        final String expected = System.getProperty("user.dir");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public final void testGetCwd_After_Change_Directory() {
        final File rootDir = new File("/");
        final SvnTool svnTool = new SvnTool();
        svnTool.cd(rootDir);
        final String actual = svnTool.getCwd();
        final String expected = rootDir.getAbsolutePath();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public final void testExecute() throws IllegalArgumentException, SVNToolException {
        final SvnTool svnTool = new SvnTool();
        final SvnToolResult result = svnTool.execute(new String[] { "log",
                "http://svn.apache.org/repos/asf/subversion/trunk/tools/hook-scripts" });
        Assert.assertEquals(0, result.getExitCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testExecute_Args_Must_Not_Be_Null() throws IllegalArgumentException, SVNToolException {
        new SvnTool().execute(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testExecute_Args_Must_Not_Be_Null_2() throws IllegalArgumentException, SVNToolException {
        new SvnTool().execute(null, null, null);
    }

    @Test
    public final void testExecute_stdout_stdout() throws IllegalArgumentException, SVNToolException {
        final SvnTool svnTool = new SvnTool();
        final int exitCode = svnTool.execute(new String[] { "log",
                "http://svn.apache.org/repos/asf/subversion/trunk/tools/hook-scripts" }, System.out, System.out);
        Assert.assertEquals(0, exitCode);
    }

    @Test
    public final void testExecute_Bad_Command_And_Null_Stdout() throws IllegalArgumentException, SVNToolException {
        final SvnTool svnTool = new SvnTool();
        final int exitCode = svnTool.execute(new String[] { "log", "wtf://svn.paissad.net/misc/stuffs" }, null,
                System.err);
        Assert.assertNotEquals(0, exitCode);
    }

    @Test
    public final void testExecute_Bad_Command_And_Null_Stderr() throws IllegalArgumentException, SVNToolException {
        final SvnTool svnTool = new SvnTool();
        final int exitCode = svnTool.execute(new String[] { "log", "wtf://svn.paissad.net/misc/stuffs" }, System.out,
                null);
        Assert.assertNotEquals(0, exitCode);
    }

}
