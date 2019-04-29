package basicWeatherInterpolation.localizationService;

import basicWeatherInterpolation.weatherProviders.ImgwWeatherProvider;
import basicWeatherInterpolation.weatherProviders.WeatherProvider;
import org.junit.Assert;
import org.junit.Test;
import weatherCollector.coordinates.Coords;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LocalizationServiceTest {

    @Test
    public void getClosestWeatherProviders() throws NoSuchFieldException, IllegalAccessException {
        //given
        LocalizationService service = new LocalizationService(4);
        List<WeatherProvider> weatherProviders = new ArrayList<>();
        String[] cities = {"Bialystok", "Bielsko Biala",  "Czestochowa", "Gdansk", "Hel", "Kasprowy Wierch",
                "Katowice", "Kielce", "Kolobrzeg", "Krakow", "Lesko", "Lodz", "Olsztyn", "Opole",
                "Przemysl", "Szczecin", "Warszawa", "Wroclaw"};

        for(String city:cities){
            weatherProviders.add(new ImgwWeatherProvider(city, "city"));
        }

        String fieldName = "weatherProviders";
        Coords coords = new Coords(52.22977f, 21.01178f); //Of Warsaw

        //when
        Class cls = service.getClass();
        Field field = cls.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(service, weatherProviders);

        //then
        List<WeatherProvider> closest = service.getClosestWeatherProviders(coords);
        Assert.assertArrayEquals(
                new ImgwWeatherProvider[]{
                        new ImgwWeatherProvider("Warszawa", "city"),
                        new ImgwWeatherProvider("Kielce", "city"),
                        new ImgwWeatherProvider("Lodz", "city"),
                        new ImgwWeatherProvider("Olsztyn", "city")},
                closest.toArray());

    }
}