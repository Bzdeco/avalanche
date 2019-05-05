package basicWeatherInterpolation.weatherProviders;

public enum PeakName{
    BANIKOV("Baníkov"),
    BARANEC("Baranec"),
    CHOPOK("Chopok"),
    DERESE("Derese"),
    DUMBIER("Dumbier"),
    GERLACH("Gerlach"),
    GIEWONT("Giewont"),
    GUBALOWKA("Gubałówka"),
    KASPROWY_WIERCH("Kasprowy Wierch"),
    KOSCIELEC("Koscielec"),
    KRIVAN("Krivan"),
    MNICH("Mnich (mountain)"),
    OSTRY_ROHAC("Ostry Rohac"),
    RYSY("Rysy");

    private String name;

    PeakName(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}