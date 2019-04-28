package basicWeatherInterpolation.weatherProviders;

import weatherCollector.coordinates.Coords;
import weatherCollector.entities.Weather;

public interface WeatherProvider {

   Coords getCoordinates();
   Weather currentWeather();
}
