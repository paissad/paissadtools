package net.paissad.paissadtools.geolocation.exception;

import net.paissad.paissadtools.api.IToolException;

import org.junit.Assert;
import org.junit.Test;

public class GeoToolExceptionTest {
    
    @Test
    public final void isIntanceOf_IToolException() {
        final Exception e = new GeoToolException();
        Assert.assertTrue(e instanceof IToolException);
    }


    @Test(expected = GeoToolException.class)
    public final void testGeoToolException() throws GeoToolException {
        throw new GeoToolException();
    }

    @Test(expected = GeoToolException.class)
    public final void testGeoToolExceptionString() throws GeoToolException {
        throw new GeoToolException("..");
    }

    @Test(expected = GeoToolException.class)
    public final void testGeoToolExceptionThrowable() throws GeoToolException {
        throw new GeoToolException(new Exception());
    }

    @Test(expected = GeoToolException.class)
    public final void testGeoToolExceptionStringThrowable() throws GeoToolException {
        throw new GeoToolException("..", new Exception());
    }
}
