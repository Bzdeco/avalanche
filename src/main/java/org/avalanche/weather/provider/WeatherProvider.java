package org.avalanche.weather.provider;

import org.avalanche.weather.coordinates.Coords;
import org.avalanche.weather.entities.Weather;

import java.io.IOException;

public interface WeatherProvider {
    Coords getCoordinates();
    Weather currentWeather() throws IOException;
}
