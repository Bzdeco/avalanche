package basicWeatherInterpolation.weatherProviders;

import weatherCollector.coordinates.Coords;
import weatherCollector.entities.Weather;

import java.io.IOException;

public interface WeatherProvider {

   Coords getCoordinates();
   Weather currentWeather() throws IOException;
}
