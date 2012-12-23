package net.paissad.paissadtools.geolocation;

import java.io.File;

public interface TestConstants {

    /** GeoLiteCity.dat file. */
    File   GEOLITECITY_DAT_FILE = new File("src/test/resources/GeoLiteCity.dat");

    /** GeoIP.dat file. */
    File   GEOIP_DAT_FILE       = new File("src/test/resources/GeoIP.dat");

    /** GeoIPv6.dat file. */
    File   GEOIP_V6_DAT_FILE    = new File("src/test/resources/GeoIPv6.dat");

    String GOOGLE_IP_ADDRESS    = "74.125.230.192";

    String YAHOO_IP_ADDRESS     = "98.138.253.109";

}
