package net.paissad.paissatools.mail.exception;

import net.paissad.paissadtools.api.IToolException;
import net.paissad.paissadtools.mail.exception.MailToolException;

import org.junit.Assert;
import org.junit.Test;

public class MailToolExceptionTest {

    @Test
    public final void isIntanceOf_IToolException() {
        final Exception e = new MailToolException();
        Assert.assertTrue(e instanceof IToolException);
    }

    @Test(expected = MailToolException.class)
    public final void testMailToolException() throws MailToolException {
        throw new MailToolException();
    }

    @Test(expected = MailToolException.class)
    public final void testMailToolExceptionString() throws MailToolException {
        throw new MailToolException("..");
    }

    @Test(expected = MailToolException.class)
    public final void testMailToolExceptionThrowable() throws MailToolException {
        throw new MailToolException(new Exception());
    }

    @Test(expected = MailToolException.class)
    public final void testMailToolExceptionStringThrowable() throws MailToolException {
        throw new MailToolException("..", new Exception());
    }

}
