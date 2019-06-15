package org.avalanche.weather.provider;

public enum LocationType {
    CITY("city"),
    PEAK("peak"),
    LAKE("lake"),
    EMPTY("");


    private final String typeValue;

    LocationType(String typeValue) {
        this.typeValue = typeValue;
    }

    @Override
    public String toString() {
        return typeValue;
    }
}
