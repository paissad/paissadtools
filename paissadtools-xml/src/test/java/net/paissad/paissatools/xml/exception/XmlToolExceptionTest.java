package net.paissad.paissatools.xml.exception;

import net.paissad.paissadtools.api.IToolException;
import net.paissad.paissadtools.xml.exception.XmlToolException;

import org.junit.Assert;
import org.junit.Test;

public class XmlToolExceptionTest {

    @Test
    public final void isIntanceOf_IToolException() {
        final Exception e = new XmlToolException();
        Assert.assertTrue(e instanceof IToolException);
    }

    @Test(expected = XmlToolException.class)
    public final void testXmlToolException() throws XmlToolException {
        throw new XmlToolException();
    }

    @Test(expected = XmlToolException.class)
    public final void testXmlToolExceptionString() throws XmlToolException {
        throw new XmlToolException("..");
    }

    @Test(expected = XmlToolException.class)
    public final void testXmlToolExceptionThrowable() throws XmlToolException {
        throw new XmlToolException(new Exception());
    }

    @Test(expected = XmlToolException.class)
    public final void testXmlToolExceptionStringThrowable() throws XmlToolException {
        throw new XmlToolException("..", new Exception());
    }

}
