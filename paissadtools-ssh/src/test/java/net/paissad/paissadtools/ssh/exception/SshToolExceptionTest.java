package net.paissad.paissadtools.ssh.exception;

import net.paissad.paissadtools.api.IToolException;

import org.junit.Assert;
import org.junit.Test;

public class SshToolExceptionTest {

    @Test
    public final void isIntanceOf_IToolException() {
        final Exception e = new SshToolException();
        Assert.assertTrue(e instanceof IToolException);
    }

    @Test(expected = SshToolException.class)
    public final void testSshToolException() throws SshToolException {
        throw new SshToolException();
    }

    @Test(expected = SshToolException.class)
    public final void testSshToolExceptionString() throws SshToolException {
        throw new SshToolException("..");
    }

    @Test(expected = SshToolException.class)
    public final void testSshToolExceptionThrowable() throws SshToolException {
        throw new SshToolException(new Exception());
    }

    @Test(expected = SshToolException.class)
    public final void testSshToolExceptionStringThrowable() throws SshToolException {
        throw new SshToolException("..", new Exception());
    }

}
