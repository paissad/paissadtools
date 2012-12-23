package net.paissad.paissadtools.ftp.exception;

import net.paissad.paissadtools.api.IToolException;

import org.junit.Assert;
import org.junit.Test;

public class FtpToolExceptionTest {

    @Test
    public final void isIntanceOf_IToolException() {
        final Exception e = new FtpToolException();
        Assert.assertTrue(e instanceof IToolException);
    }

    @Test(expected = FtpToolException.class)
    public final void testFtpToolException() throws FtpToolException {
        throw new FtpToolException();
    }

    @Test(expected = FtpToolException.class)
    public final void testFtpToolExceptionString() throws FtpToolException {
        throw new FtpToolException("..");
    }

    @Test(expected = FtpToolException.class)
    public final void testFtpToolExceptionThrowable() throws FtpToolException {
        throw new FtpToolException(new Exception());
    }

    @Test(expected = FtpToolException.class)
    public final void testFtpToolExceptionStringThrowable() throws FtpToolException {
        throw new FtpToolException("..", new Exception());
    }

}
