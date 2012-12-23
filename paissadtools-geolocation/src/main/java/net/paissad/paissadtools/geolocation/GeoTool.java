package net.paissad.paissadtools.geolocation;

import java.io.File;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Date;

import net.paissad.paissadtools.api.ITool;
import net.paissad.paissadtools.geolocation.exception.GeoToolException;

import com.maxmind.geoip.Country;
import com.maxmind.geoip.DatabaseInfo;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

/**
 * Geolocation tool for processing IP address lookup in order to retrieve countries, cities, latitudes, longitudes & so
 * forth.
 * 
 * @author paissad
 */
public class GeoTool implements ITool {

    /**
     * Process a IP address lookup. Works for IPV4 & IPV6 addresses.
     * 
     * @param ipAddress - The IP address.
     * @param databaseFile - The GEOIP database file to use : <b><span style='color:red'>GeoLiteCity[v6].dat</span></b>
     *            or <b><span style='color:red'>GeoIP[v6].dat</span></b> file.
     * @return The lookup result. Never <code>null</code>.
     * @throws IllegalArgumentException If the specified IP address is <code>null</code> or blank.
     * @throws GeoToolException If an error occurs while processing the IP address lookup.
     * @see #lookup(String, File, boolean)
     * @see GeoToolResult
     */
    public GeoToolResult lookup(final String ipAddress, final File databaseFile) throws IllegalArgumentException,
            GeoToolException {
        return this.lookup(ipAddress, databaseFile, false);
    }

    /**
     * Process a IP address lookup. Works for IPV4 & IPV6 addresses.
     * 
     * @param ipAddress - The IP address.
     * @param databaseFile - The GEOIP database file to use : <b><span style='color:red'>GeoLiteCity[v6].dat</span></b>
     *            or <b><span style='color:red'>GeoIP[v6].dat</span></b> file.
     * @param loadIntoMemory - Whether or not to load the database into memory (RAM) or read it only from the disk.
     * @return The lookup result. Never <code>null</code>.
     * @throws IllegalArgumentException If the specified IP address is <code>null</code> or blank.
     * @throws GeoToolException If an error occurs while processing the IP address lookup.
     * @see #lookup(String, File)
     * @see GeoToolResult
     */
    public GeoToolResult lookup(final String ipAddress, final File databaseFile, final boolean loadIntoMemory)
            throws IllegalArgumentException, GeoToolException {

        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("The IP address cannot be null or blank.");
        }

        try {
            final InetAddress addr = InetAddress.getByName(ipAddress);
            final boolean isIPV6 = addr instanceof Inet6Address;

            final int loadOption = (loadIntoMemory) ? LookupService.GEOIP_MEMORY_CACHE : LookupService.GEOIP_STANDARD;

            final LookupService lookupService = new LookupService(databaseFile, loadOption);

            final DatabaseInfo databaseInfo = lookupService.getDatabaseInfo();
            final int databaseType = databaseInfo.getType();

            final GeoToolResultImpl result = new GeoToolResultImpl();

            if (databaseType == DatabaseInfo.COUNTRY_EDITION || databaseType == DatabaseInfo.COUNTRY_EDITION_V6) {
                // GeoIP.dat
                final Country country = (isIPV6) ? lookupService.getCountryV6(ipAddress) : lookupService
                        .getCountry(ipAddress);
                result.setCountryName(country.getName());
                result.setCountryCode(country.getCode());

            } else {
                // Supposed to be GeoLiteCity.dat, otherwise an error will occur.
                final Location location = (isIPV6) ? lookupService.getLocationV6(ipAddress) : lookupService
                        .getLocation(ipAddress);
                result.setCountryName(location.countryName);
                result.setCountryCode(location.countryCode);
                result.setCity(location.city);
                result.setPostalCode(location.postalCode);
                result.setLatitude(Double.valueOf(location.latitude));
                result.setLongitude(Double.valueOf(location.longitude));
            }

            result.setIpAddress(ipAddress);
            return result;

        } catch (final Exception e) {
            throw new GeoToolException("An error occurred while processing lookup for the ip address '" + ipAddress
                    + "' : " + e.getMessage(), e);
        }
    }

    /**
     * Returns the date of the database file.
     * 
     * @param databaseFile - The GEOIP database file to use : <b><span style='color:red'>GeoLiteCity[v6].dat</span></b>
     *            or <b><span style='color:red'>GeoIP[v6].dat</span></b> file.
     * @return The date of the database file.
     * @throws GeoToolException If an error occurs while retrieving the date of the database.
     */
    public Date getDatabaseDate(final File databaseFile) throws GeoToolException {
        try {
            final LookupService lookupService = new LookupService(databaseFile);
            return lookupService.getDatabaseInfo().getDate();

        } catch (final Exception e) {
            throw new GeoToolException("Error while retrieving the date of the database : " + e.getMessage(), e);
        }
    }
}