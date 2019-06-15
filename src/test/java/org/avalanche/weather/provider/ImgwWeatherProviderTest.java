package org.avalanche.weather.provider;

import org.avalanche.weather.coordinates.Coords;
import org.avalanche.weather.entities.Weather;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class ImgwWeatherProviderTest {

    @Test
    public void shouldReturnCorrectCoordinatesOfZakopane() {
        //given
        WeatherProvider imgwWP = new ImgwWeatherProvider(ProvidersName.ZAKOPANE, LocationType.CITY);

        //when
        Coords expected = new Coords(49.2969446f, 19.950659f);

        //then
        assertEquals(expected, imgwWP.getCoordinates());
    }

    @Test
    public void shouldReturnCorrectCoordinatesOfKasprowyWierch() {
        //given
        WeatherProvider imgwWP = new ImgwWeatherProvider(ProvidersName.KASPROWY_WIERCH, LocationType.PEAK);

        //when
        Coords expected = new Coords(49.2318014f, 19.9815609f);

        //then
        assertEquals(expected, imgwWP.getCoordinates());
    }



    @Test
    @Disabled
    public void getCurrentWeather() throws IOException, ParseException {
        // Data should be parsed from:
        // https://danepubliczne.imgw.pl/api/data/synop/station/kasprowywierch/format/json

        //given
        WeatherProvider imgwWP = new ImgwWeatherProvider(ProvidersName.KASPROWY_WIERCH, LocationType.PEAK);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse("2019-04-23, 18:00:00", formatter);

        Weather expected = new Weather();
        expected.setTime(date);
        expected.setTemp(-1f);
        expected.setWindSpeed(10f);
        expected.setWindDeg(150f);
        expected.setHumidity(98.5f);
        expected.setSnow(2.7f);

        //then
        assertEquals(expected, imgwWP.currentWeather());
    }
}