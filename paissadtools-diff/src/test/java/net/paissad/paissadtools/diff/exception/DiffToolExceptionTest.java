package net.paissad.paissadtools.diff.exception;

import net.paissad.paissadtools.api.IToolException;

import org.junit.Assert;
import org.junit.Test;

public class DiffToolExceptionTest {

    @Test
    public final void isIntanceOf_IToolException() {
        final Exception e = new DiffToolException();
        Assert.assertTrue(e instanceof IToolException);
    }

    @Test(expected = DiffToolException.class)
    public final void testDiffToolException() throws DiffToolException {
        throw new DiffToolException();
    }

    @Test(expected = DiffToolException.class)
    public final void testDiffToolExceptionString() throws DiffToolException {
        throw new DiffToolException("..");
    }

    @Test(expected = DiffToolException.class)
    public final void testDiffToolExceptionThrowable() throws DiffToolException {
        throw new DiffToolException(new Exception());
    }

    @Test(expected = DiffToolException.class)
    public final void testDiffToolExceptionStringThrowable() throws DiffToolException {
        throw new DiffToolException("..", new Exception());
    }

}
