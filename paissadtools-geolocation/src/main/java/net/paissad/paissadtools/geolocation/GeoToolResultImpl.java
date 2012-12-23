package net.paissad.paissadtools.geolocation;

/**
 * Simple & internal implementation of {@link GeoToolResult}.
 * 
 * @author paissad
 */
class GeoToolResultImpl implements GeoToolResult {

    private String ipAddress;
    private String countryName;
    private String countryCode;
    private String city;
    private String postalCode;
    private Double latitude;
    private Double longitude;

    GeoToolResultImpl() {
    }

    @Override
    public String getIpAddress() {
        return this.ipAddress;
    }

    @Override
    public String getCountryName() {
        return this.countryName;
    }

    @Override
    public String getCountryCode() {
        return this.countryCode;
    }

    @Override
    public String getCity() {
        return this.city;
    }

    @Override
    public String getPostalCode() {
        return this.postalCode;
    }

    @Override
    public Double getLatitude() {
        return this.latitude;
    }

    @Override
    public Double getLongitude() {
        return this.longitude;
    }

    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setCountryName(final String countryName) {
        this.countryName = countryName;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    public void setLatitude(final Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(final Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[ipAddress=").append(this.ipAddress).append(", countryName=").append(this.countryName)
                .append(", countryCode=").append(this.countryCode).append(", city=").append(this.city)
                .append(", postalCode=").append(this.postalCode).append(", latitude=").append(this.latitude)
                .append(", longitude=").append(this.longitude).append("]");
        return sb.toString();
    }

}
