package net.paissad.paissadtools.compress.exception;

import net.paissad.paissadtools.api.IToolException;

import org.junit.Assert;
import org.junit.Test;

public class CompressExceptionTest {

    @Test
    public final void isIntanceOf_IToolException() {
        final Exception e = new CompressException();
        Assert.assertTrue(e instanceof IToolException);
    }

    @Test(expected = CompressException.class)
    public final void testCompressException() throws CompressException {
        throw new CompressException();
    }

    @Test(expected = CompressException.class)
    public final void testCompressExceptionString() throws CompressException {
        throw new CompressException("..");
    }

    @Test(expected = CompressException.class)
    public final void testCompressExceptionThrowable() throws CompressException {
        throw new CompressException(new Exception());
    }

    @Test(expected = CompressException.class)
    public final void testCompressExceptionStringThrowable() throws CompressException {
        throw new CompressException("", new Exception());
    }

}
