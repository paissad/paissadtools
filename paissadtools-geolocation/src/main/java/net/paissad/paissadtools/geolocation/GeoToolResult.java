package net.paissad.paissadtools.geolocation;

/**
 * Contains the result of a geolocation process lookup : country, city, latitude, longitude ...
 * 
 * @author paissad
 */
public interface GeoToolResult {

    String getIpAddress();

    String getCountryName();

    String getCountryCode();

    String getCity();

    String getPostalCode();

    Double getLatitude();

    Double getLongitude();
}
