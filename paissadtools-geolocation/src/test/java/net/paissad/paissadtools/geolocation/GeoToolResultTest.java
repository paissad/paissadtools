package net.paissad.paissadtools.geolocation;

import static net.paissad.paissadtools.geolocation.TestConstants.GEOIP_DAT_FILE;
import static net.paissad.paissadtools.geolocation.TestConstants.GEOLITECITY_DAT_FILE;
import static net.paissad.paissadtools.geolocation.TestConstants.GOOGLE_IP_ADDRESS;
import static net.paissad.paissadtools.geolocation.TestConstants.YAHOO_IP_ADDRESS;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class GeoToolResultTest {

    private static final GeoTool GEO_TOOL = new GeoTool();

    private static GeoToolResult countryLookupResult;
    private static GeoToolResult cityLookupResult;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        countryLookupResult = GEO_TOOL.lookup(YAHOO_IP_ADDRESS, GEOIP_DAT_FILE);
        cityLookupResult = GEO_TOOL.lookup(GOOGLE_IP_ADDRESS, GEOLITECITY_DAT_FILE);
    }

    @Test
    public final void testGetIpAddress() {
        Assert.assertEquals(countryLookupResult.getIpAddress(), YAHOO_IP_ADDRESS);
        Assert.assertEquals(cityLookupResult.getIpAddress(), GOOGLE_IP_ADDRESS);
    }

    @Test
    public final void testGetCountryName() {
        Assert.assertEquals(countryLookupResult.getCountryName(), "United States");
        Assert.assertEquals(cityLookupResult.getCountryName(), "United States");
    }

    @Test
    public final void testGetCountryCode() {
        Assert.assertEquals(countryLookupResult.getCountryCode(), "US");
        Assert.assertEquals(cityLookupResult.getCountryCode(), "US");
    }

    @Test
    public final void testGetCity() {
        Assert.assertNull(countryLookupResult.getCity());
        Assert.assertEquals(cityLookupResult.getCity(), "Mountain View");
    }

    @Test
    public final void testGetPostalCode() {
        Assert.assertNull(countryLookupResult.getPostalCode());
        Assert.assertEquals(cityLookupResult.getPostalCode(), "94043");
    }

    @Test
    public final void testGetLatitude() {
        Assert.assertNull(countryLookupResult.getLatitude());
        Assert.assertEquals(cityLookupResult.getLatitude().doubleValue(), 37.41920471191406, 0.0000000000001);
    }

    @Test
    public final void testGetLongitude() {
        Assert.assertNull(countryLookupResult.getLongitude());
        Assert.assertEquals(cityLookupResult.getLongitude().doubleValue(), -122.05740356445312, 0.0000000000001);
    }

    @Test
    public final void testToString() {
        Assert.assertNotNull(countryLookupResult.toString());
        Assert.assertNotNull(cityLookupResult.toString());
    }

}
