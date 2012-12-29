package net.paissad.paissadtools.api;

import org.junit.Test;

public class IToolExceptionTest {

    @Test(expected = IToolException.class)
    public final void testIToolException() throws IToolException {
        throw new IToolException();
    }

    @Test(expected = IToolException.class)
    public final void testIToolExceptionString() throws IToolException {
        throw new IToolException("..");
    }

    @Test(expected = IToolException.class)
    public final void testIToolExceptionThrowable() throws IToolException {
        throw new IToolException(new Exception());
    }

    @Test(expected = IToolException.class)
    public final void testIToolExceptionStringThrowable() throws IToolException {
        throw new IToolException("..", new Exception());
    }

}
