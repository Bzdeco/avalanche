package org.avalanche.weather.provider;

public enum ProvidersName {
    ZAKOPANE("ZAKOPANE"),
    HALA_GASIENICOWA("Hala Gasienicowa"),
    PORONIN("PORONIN"),
    BUKOWINA_TATRZANSKA("Bukowina Tatrzanska"),
    MORSKIE_OKO("Morskie Oko"),
    POLANA_CHOCHOLOWSKA("Polana Chocholowska"),
    KASPROWY_WIERCH("Kasprowy Wierch"),
    DOLINA_PIECIU_STAWOW("Dolina Pieciu Stawow");


    private String name;

    ProvidersName(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
