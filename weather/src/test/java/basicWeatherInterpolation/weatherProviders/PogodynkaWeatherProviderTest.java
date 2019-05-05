package basicWeatherInterpolation.weatherProviders;

import org.junit.Test;
import weatherCollector.coordinates.Coords;
import weatherCollector.entities.Weather;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.Assert.assertEquals;

public class PogodynkaWeatherProviderTest {

    @Test
    public void ShouldReturnCorrectCoordinatesOfZakopane() {
        //given
        WeatherProvider pogodynkaWP = new PogodynkaWeatherProvider(ProvidersName.ZAKOPANE, LocationType.CITY);

        //when
        Coords expected = new Coords(49.2969446f, 19.950659f);

        //then
        assertEquals(expected, pogodynkaWP.getCoordinates());
    }

    @Test
    public void ShouldReturnCorrectCoordinatesOfKasprowyWierch() {
        //given
        WeatherProvider pogodynkaWP = new PogodynkaWeatherProvider(ProvidersName.KASPROWY_WIERCH, LocationType.PEAK);

        //when
        Coords expected = new Coords(49.2318014f, 19.9815609f);

        //then
        assertEquals(expected, pogodynkaWP.getCoordinates());
    }

    @Test
    public void ShouldReturnCorrectWeatherForDolinaPieciuStawow() throws IOException {
        //given
        WeatherProvider pogodynkaWP = new PogodynkaWeatherProvider(ProvidersName.DOLINA_PIECIU_STAWOW, LocationType.EMPTY);
        long date = System.currentTimeMillis();


        //Data should be looked up on website
        //when
        Weather expected = new Weather();
        expected.setTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.of("Europe/Warsaw")));
        expected.setTemp(-0.2f);
        expected.setTempMax(-0.2f);
        expected.setTempMin(-0.2f);
        expected.setWindSpeed(2.5f);
        expected.setWindDeg(315.0f);
        expected.setSnow(8f);

        //then
        Weather result = pogodynkaWP.currentWeather();
        result.setTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.of("Europe/Warsaw")));
        assertEquals(expected, result);
    }

}