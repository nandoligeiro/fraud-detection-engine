package br.com.nandoligeiro.frauddetection.domain.transaction.model.vo;

import java.util.Optional;

public record GeoLocation(
        String country,
        String city,
        Double latitude,
        Double longitude
) {

    public GeoLocation {
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("country must not be blank");
        }
        if ((latitude == null) != (longitude == null)) {
            throw new IllegalArgumentException("latitude and longitude must be provided together");
        }
        if (latitude != null && (latitude < -90 || latitude > 90)) {
            throw new IllegalArgumentException("latitude must be between -90 and 90");
        }
        if (longitude != null && (longitude < -180 || longitude > 180)) {
            throw new IllegalArgumentException("longitude must be between -180 and 180");
        }
    }

    public static GeoLocation of(String country, String city, Double latitude, Double longitude) {
        return new GeoLocation(country, city, latitude, longitude);
    }

    public boolean isInternationalComparedTo(String homeCountry) {
        if (homeCountry == null || homeCountry.isBlank()) {
            return false;
        }
        return !country.equalsIgnoreCase(homeCountry);
    }

    public Optional<String> cityAsOptional() {
        return Optional.ofNullable(city).filter(value -> !value.isBlank());
    }

    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }
}
