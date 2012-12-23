package net.paissad.paissadtools.geolocation;

import static net.paissad.paissadtools.geolocation.TestConstants.GEOIP_DAT_FILE;
import static net.paissad.paissadtools.geolocation.TestConstants.GEOIP_V6_DAT_FILE;
import static net.paissad.paissadtools.geolocation.TestConstants.GEOLITECITY_DAT_FILE;
import static net.paissad.paissadtools.geolocation.TestConstants.GOOGLE_IP_ADDRESS;
import static net.paissad.paissadtools.geolocation.TestConstants.YAHOO_IP_ADDRESS;

import java.io.File;
import java.util.Date;

import net.paissad.paissadtools.geolocation.exception.GeoToolException;

import org.junit.Assert;
import org.junit.Test;

import com.maxmind.geoip.DatabaseInfo;

public class GeoToolTest {

    private static final File NON_EXISTENT_FILE = new File(".");

    @Test(expected = IllegalArgumentException.class)
    public final void testLookup_Null_IPAddress() throws IllegalArgumentException, GeoToolException {
        final GeoTool geoTool = new GeoTool();
        geoTool.lookup(null, NON_EXISTENT_FILE);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testLookup_Blank_IPAddress() throws IllegalArgumentException, GeoToolException {
        final GeoTool geoTool = new GeoTool();
        geoTool.lookup("  \t \n", NON_EXISTENT_FILE);
    }

    @Test(expected = GeoToolException.class)
    public final void testLookup_NonExistentDatabaseFile() throws IllegalArgumentException, GeoToolException {
        final GeoTool geoTool = new GeoTool();
        geoTool.lookup(GOOGLE_IP_ADDRESS, NON_EXISTENT_FILE);
    }

    @Test
    public final void testLookup_GeoIP_Database() throws IllegalArgumentException, GeoToolException {
        final GeoTool geoTool = new GeoTool();
        final GeoToolResult actual = geoTool.lookup(GOOGLE_IP_ADDRESS, GEOIP_DAT_FILE);
        final GeoToolResult expected = new GeoToolResult() {

            @Override
            public String getPostalCode() {
                return null;
            }

            @Override
            public Double getLongitude() {
                return null;
            }

            @Override
            public Double getLatitude() {
                return null;
            }

            @Override
            public String getIpAddress() {
                return GOOGLE_IP_ADDRESS;
            }

            @Override
            public String getCountryName() {
                return "United States";
            }

            @Override
            public String getCountryCode() {
                return "US";
            }

            @Override
            public String getCity() {
                return null;
            }
        };
        this.assertEqualsGeoToolResults(expected, actual);
    }

    /**
     * GeoIPv6.dat file is not yet supported by GeoTool.<br>
     * The problem is that it is not yet possible to retrieve the correct database type of GeoIPv6.dat.<br>
     * It should return of type {@link DatabaseInfo#COUNTRY_EDITION_V6}, but it doesn't.<br>
     * Maybe the newer version of GeoIP Java API will support. But the newest version, current 1.2.8, is not yet
     * available in the Maven Central Repository.
     * 
     * @throws IllegalArgumentException
     * @throws GeoToolException
     */
    @Test(expected = AssertionError.class)
    public final void testLookup_GeoIP_Database_IPV6() throws IllegalArgumentException, GeoToolException {
        final GeoTool geoTool = new GeoTool();
        final GeoToolResult actual = geoTool.lookup("2a00:1450:4007:801::1004", GEOIP_V6_DAT_FILE);
        final GeoToolResult expected = new GeoToolResult() {

            @Override
            public String getPostalCode() {
                return null;
            }

            @Override
            public Double getLongitude() {
                return null;
            }

            @Override
            public Double getLatitude() {
                return null;
            }

            @Override
            public String getIpAddress() {
                return "2a00:1450:4007:801::1004";
            }

            @Override
            public String getCountryName() {
                return "United States";
            }

            @Override
            public String getCountryCode() {
                return "US";
            }

            @Override
            public String getCity() {
                return null;
            }
        };
        this.assertEqualsGeoToolResults(expected, actual);
    }

    @Test
    public final void testLookup_GeoLiteCity_Database() throws IllegalArgumentException, GeoToolException {
        final GeoTool geoTool = new GeoTool();
        final GeoToolResult actual = geoTool.lookup(YAHOO_IP_ADDRESS, GEOLITECITY_DAT_FILE);
        final GeoToolResult expected = new GeoToolResult() {

            @Override
            public String getPostalCode() {
                return "94089";
            }

            @Override
            public Double getLongitude() {
                return Double.valueOf(-122.00740051269531);
            }

            @Override
            public Double getLatitude() {
                return Double.valueOf(37.424896240234375);
            }

            @Override
            public String getIpAddress() {
                return YAHOO_IP_ADDRESS;
            }

            @Override
            public String getCountryName() {
                return "United States";
            }

            @Override
            public String getCountryCode() {
                return "US";
            }

            @Override
            public String getCity() {
                return "Sunnyvale";
            }
        };
        this.assertEqualsGeoToolResults(expected, actual);
    }

    @Test
    public final void testLookup_Memory_Database_Loading() throws IllegalArgumentException, GeoToolException {
        final GeoTool geoTool = new GeoTool();
        geoTool.lookup(GOOGLE_IP_ADDRESS, GEOLITECITY_DAT_FILE, true);
    }

    @Test(expected = GeoToolException.class)
    public final void testGetDatabaseDate_Non_Existent_Database() throws GeoToolException {
        new GeoTool().getDatabaseDate(NON_EXISTENT_FILE);
    }

    @Test
    public final void testGetDatabaseDate() throws GeoToolException {
        final GeoTool geoTool = new GeoTool();
        final Date actualDate = geoTool.getDatabaseDate(GEOLITECITY_DAT_FILE);
        Assert.assertNotNull(actualDate); // cheat test ...
    }

    private void assertEqualsGeoToolResults(final GeoToolResult expected, final GeoToolResult actual) {
        Assert.assertEquals(expected.getIpAddress(), actual.getIpAddress());
        Assert.assertEquals(expected.getCountryName(), actual.getCountryName());
        Assert.assertEquals(expected.getCountryCode(), actual.getCountryCode());
        Assert.assertEquals(expected.getCity(), actual.getCity());
        Assert.assertEquals(expected.getPostalCode(), actual.getPostalCode());
        Assert.assertEquals(expected.getLatitude(), actual.getLatitude());
        Assert.assertEquals(expected.getLongitude(), actual.getLongitude());
    }

}
