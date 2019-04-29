package basicWeatherInterpolation.weatherProviders;

public enum ProvidersName {
    Zakopane("Zakopane"),
    HaleGasienicowa("Hala Gasienicowa"),
    Poronin("Poronin"),
    BukowinaTatrzanska("Bukowina Tatrzanska"),
    MorskieOko("Morskie Oko"),
    PolanaChocholowska ("Polana Chocholowska"),
    KasprowyWierch("Kasprowy Wierch"),
    DolinaPieciuStawow("Dolina Pieciu Stawow");


    private String name;

    ProvidersName(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
