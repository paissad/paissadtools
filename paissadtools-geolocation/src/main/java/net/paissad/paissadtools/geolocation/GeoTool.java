package net.paissad.paissadtools.geolocation;

import java.io.File;
import java.net.Inet6Address;
import java.net.InetAddress;

import net.paissad.paissadtools.api.ITool;
import net.paissad.paissadtools.geolocation.exception.GeoToolException;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

/**
 * Geolocation tool for processing ip address lookup in order to retrieve countries, cities, latitudes, longitudes & so
 * forth.
 * 
 * @author paissad
 */
public class GeoTool implements ITool {

    /**
     * Process a IP address lookup. Works for IPV4 & IPV6 addresses.
     * 
     * @param ipAddress - The IP address.
     * @param databaseFile - The GEOIP database file to use : <b><span style='color:red'>GeoLiteCity.dat</span></b>
     *            file.
     * @return The lookup result. Never <code>null</code>.
     * @throws IllegalArgumentException If the specified IP address is <code>null</code> or blank.
     * @throws GeoToolException If an error occurs while processing the IP address lookup.
     * @see GeoToolResult
     */
    public GeoToolResult lookup(final String ipAddress, final File databaseFile) throws IllegalArgumentException,
            GeoToolException {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("The IP address cannot be null or blank");
        }
        try {
            final InetAddress addr = InetAddress.getByName(ipAddress);
            final boolean isIPV6 = (addr instanceof Inet6Address);
            final LookupService lookupService = new LookupService(databaseFile);

            final GeoToolResultImpl result = new GeoToolResultImpl();
            final Location location = (isIPV6) ? lookupService.getLocationV6(ipAddress) : lookupService
                    .getLocation(ipAddress);

            result.setIpAddress(ipAddress);

            if (location != null) {
                result.setCountryName(location.countryName);
                result.setCountryCode(location.countryCode);
                result.setCity(location.city);
                result.setPostalCode(location.postalCode);
                result.setLatitude(location.latitude);
                result.setLongitude(location.longitude);
            }

            return result;

        } catch (final Exception e) {
            throw new GeoToolException("An error occurred while processing lookup for the ip address '" + ipAddress
                    + "' : " + e.getMessage(), e);
        }
    }
}