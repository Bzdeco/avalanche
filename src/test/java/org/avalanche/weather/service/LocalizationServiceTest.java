package org.avalanche.weather.service;

import org.avalanche.weather.coordinates.Coords;
import org.avalanche.weather.provider.ImgwWeatherProvider;
import org.avalanche.weather.provider.LocationType;
import org.avalanche.weather.provider.WeatherProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LocalizationServiceTest {

    @Test
    public void getClosestWeatherProviders() {
        //given
        List<WeatherProvider> weatherProviders;
        String[] cities = {"Bialystok", "Bielsko Biala",  "Czestochowa", "Gdansk", "Hel", "Kasprowy Wierch",
                "Katowice", "Kielce", "Kolobrzeg", "Krakow", "Lesko", "Lodz", "Olsztyn", "Opole",
                "Przemysl", "Szczecin", "Warszawa", "Wroclaw"};
        weatherProviders = Arrays.stream(cities).map(city -> new ImgwWeatherProvider(city, LocationType.CITY)).collect(Collectors
                .toList());

        //when
        LocalizationService service = new LocalizationService(4, weatherProviders);
        Coords coords = new Coords(52.22977f, 21.01178f); //Of Warsaw

        //then
        List<WeatherProvider> closest = service.getClosestWeatherProviders(coords);
        Assertions.assertArrayEquals(
                new ImgwWeatherProvider[]{
                        new ImgwWeatherProvider("Warszawa",   LocationType.CITY),
                        new ImgwWeatherProvider("Kielce",     LocationType.CITY),
                        new ImgwWeatherProvider("Lodz",       LocationType.CITY),
                        new ImgwWeatherProvider("Olsztyn",    LocationType.CITY)},
                closest.toArray());

    }
}