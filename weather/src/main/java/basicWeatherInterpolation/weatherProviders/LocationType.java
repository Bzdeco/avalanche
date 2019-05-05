package basicWeatherInterpolation.weatherProviders;

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