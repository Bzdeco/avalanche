package basicWeatherInterpolation.localizationService;

import basicWeatherInterpolation.weatherProviders.ImgwWeatherProvider;
import basicWeatherInterpolation.weatherProviders.LocationType;
import basicWeatherInterpolation.weatherProviders.WeatherProvider;
import org.junit.Assert;
import org.junit.Test;
import weatherCollector.coordinates.Coords;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LocalizationServiceTest {

    @Test
    public void getClosestWeatherProviders() throws NoSuchFieldException, IllegalAccessException {
        //given
        List<WeatherProvider> weatherProviders;
        String[] cities = {"Bialystok", "Bielsko Biala",  "Czestochowa", "Gdansk", "Hel", "Kasprowy Wierch",
                "Katowice", "Kielce", "Kolobrzeg", "Krakow", "Lesko", "Lodz", "Olsztyn", "Opole",
                "Przemysl", "Szczecin", "Warszawa", "Wroclaw"};
        weatherProviders = Arrays.stream(cities).map(city -> new ImgwWeatherProvider(city, LocationType.CITY)).collect(Collectors.toList());

        //when
        LocalizationService service = new LocalizationService(4, weatherProviders);
        Coords coords = new Coords(52.22977f, 21.01178f); //Of Warsaw

        //then
        List<WeatherProvider> closest = service.getClosestWeatherProviders(coords);
        Assert.assertArrayEquals(
                new ImgwWeatherProvider[]{
                        new ImgwWeatherProvider("Warszawa",   LocationType.CITY),
                        new ImgwWeatherProvider("Kielce",     LocationType.CITY),
                        new ImgwWeatherProvider("Lodz",       LocationType.CITY),
                        new ImgwWeatherProvider("Olsztyn",    LocationType.CITY)},
                closest.toArray());

    }
}