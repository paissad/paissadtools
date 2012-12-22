package net.paissad.paissadtools.example.geolocation;

import java.io.File;

import net.paissad.paissadtools.geolocation.GeoTool;
import net.paissad.paissadtools.geolocation.GeoToolResult;
import net.paissad.paissadtools.geolocation.exception.GeoToolException;

/**
 * Sample code of how to process a simple IP address lookup.
 * 
 * @author paissad
 */
public class GeoipLookupExample {

    public static void main(final String[] args) throws GeoToolException {

        final File databaseFile = new File("src/main/resources/geolocation/GeoliteCity.dat");

        final GeoToolResult ipv4Result = new GeoTool().lookup("74.125.230.192", databaseFile);
        System.out.println(ipv4Result);

        final GeoToolResult ipv6Result = new GeoTool().lookup("2a00:1450:4007:801::1006", databaseFile);
        System.out.println(ipv6Result);

        final GeoToolResult hostnameResult = new GeoTool().lookup("google.com", databaseFile);
        System.out.println(hostnameResult);
    }
}
