package basicWeatherInterpolation.weatherProviders;

import org.junit.Test;
import weatherCollector.coordinates.Coords;
import weatherCollector.entities.Weather;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class PogodynkaWeatherProviderTest {

    @Test
    public void ShouldReturnCorrectCoordinatesOfZakopane() {
        //given
        WeatherProvider pogodynkaWP = new PogodynkaWeatherProvider("Zakopane", "city");

        //when
        Coords expected = new Coords(49.2969446f, 19.950659f);

        //then
        assertEquals(expected, pogodynkaWP.getCoordinates());
    }

    @Test
    public void ShouldReturnCorrectCoordinatesOfKasprowyWierch() {
        //given
        WeatherProvider pogodynkaWP = new PogodynkaWeatherProvider("Kasprowy Wierch", "peak");

        //when
        Coords expected = new Coords(49.2318014f, 19.9815609f);

        //then
        assertEquals(expected, pogodynkaWP.getCoordinates());
    }

    @Test
    public void ShouldReturnCorrectWeatherForDolinaPieciuStawow() throws IOException {
        //given
        WeatherProvider pogodynkaWP = new PogodynkaWeatherProvider("Dolina Pięciu Stawów", "park");
        long date = System.currentTimeMillis()/1000L;

        //when
        Weather expected = new Weather();
        expected.setTime(date);
        expected.setTemp(-0.2f);
        expected.setTempMax(-0.2f);
        expected.setTempMin(-0.2f);
        expected.setWindSpeed(2.5f);
        expected.setWinDeg(315.0f);
        expected.setSnow(8f);

        //then
        Weather result = pogodynkaWP.currentWeather();
        result.setTime(date);
        assertEquals(expected, result);
    }

}