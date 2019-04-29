package basicWeatherInterpolation.weatherProviders;

import org.junit.Test;
import weatherCollector.coordinates.Coords;
import weatherCollector.entities.Weather;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class ImgwWeatherProviderTest {

    @Test
    public void ShouldReturnCorrectCoordinatesOfZakopane() {
        //given
        WeatherProvider imgwWP = new ImgwWeatherProvider("Zakopane", "city");

        //when
        Coords expected = new Coords(49.2969446f, 19.950659f);

        //then
        assertEquals(expected, imgwWP.getCoordinates());
    }

    @Test
    public void ShouldReturnCorrectCoordinatesOfKasprowyWierch() {
        //given
        WeatherProvider imgwWP = new ImgwWeatherProvider("Kasprowy Wierch", "peak");

        //when
        Coords expected = new Coords(49.2318014f, 19.9815609f);

        //then
        assertEquals(expected, imgwWP.getCoordinates());
    }



    @Test
    public void getCurrentWeather() throws IOException, ParseException {
        // Data should be parsed from:
        // https://danepubliczne.imgw.pl/api/data/synop/station/kasprowywierch/format/json


        //given
        WeatherProvider imgwWP = new ImgwWeatherProvider("Kasprowy Wierch", "peak");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss");
        Date date = format.parse( "2019-04-23, 18:00:00");

        Weather expected = new Weather();
        expected.setTime(date.getTime()/1000L);
        expected.setTemp(-1f);
        expected.setWindSpeed(10f);
        expected.setWinDeg(150f);
        expected.setHumidity(98.5f);
        expected.setSnow(2.7f);

        //then
        assertEquals(expected, imgwWP.currentWeather());
    }
}