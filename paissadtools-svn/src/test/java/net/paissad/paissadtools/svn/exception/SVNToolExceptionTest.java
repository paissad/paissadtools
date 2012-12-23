package net.paissad.paissadtools.svn.exception;

import net.paissad.paissadtools.api.IToolException;

import org.junit.Assert;
import org.junit.Test;

public class SVNToolExceptionTest {

    @Test
    public final void isIntanceOf_IToolException() {
        final Exception e = new SVNToolException();
        Assert.assertTrue(e instanceof IToolException);
    }

    @Test(expected = SVNToolException.class)
    public final void testSVNToolException() throws SVNToolException {
        throw new SVNToolException();
    }

    @Test(expected = SVNToolException.class)
    public final void testSVNToolExceptionString() throws SVNToolException {
        throw new SVNToolException("..");
    }

    @Test(expected = SVNToolException.class)
    public final void testSVNToolExceptionThrowable() throws SVNToolException {
        throw new SVNToolException(new Exception());
    }

    @Test(expected = SVNToolException.class)
    public final void testSVNToolExceptionStringThrowable() throws SVNToolException {
        throw new SVNToolException("", new Exception());
    }

}
