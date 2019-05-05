package basicWeatherInterpolation.weatherProviders;

public enum PeakName{
    Banikov("Baníkov"),
    Baranec("Baranec"),
    Chopok("Chopok"),
    Derese("Derese"),
    Dumbier("Dumbier"),
    Gerlach("Gerlach"),
    Giewont("Giewont"),
    Gubalowka("Gubałówka"),
    KasprowyWierch("Kasprowy Wierch"),
    Koscielec("Koscielec"),
    Krivan("Krivan"),
    Mnich("Mnich (mountain)"),
    OstryRohac("Ostry Rohac"),
    Rysy("Rysy");

    private String name;

    PeakName(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}