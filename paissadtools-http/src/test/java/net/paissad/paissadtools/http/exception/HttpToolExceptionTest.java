package net.paissad.paissadtools.http.exception;

import net.paissad.paissadtools.api.IToolException;

import org.junit.Assert;
import org.junit.Test;

public class HttpToolExceptionTest {

    @Test
    public final void isIntanceOf_IToolException() {
        final Exception e = new HttpToolException();
        Assert.assertTrue(e instanceof IToolException);
    }

    @Test(expected = HttpToolException.class)
    public final void testHttpToolException() throws HttpToolException {
        throw new HttpToolException();
    }

    @Test(expected = HttpToolException.class)
    public final void testHttpToolExceptionString() throws HttpToolException {
        throw new HttpToolException("..");
    }

    @Test(expected = HttpToolException.class)
    public final void testHttpToolExceptionThrowable() throws HttpToolException {
        throw new HttpToolException(new Exception());
    }

    @Test(expected = HttpToolException.class)
    public final void testHttpToolExceptionStringThrowable() throws HttpToolException {
        throw new HttpToolException("", new Exception());
    }

}
