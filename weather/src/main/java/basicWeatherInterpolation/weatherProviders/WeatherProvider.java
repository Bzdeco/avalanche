package basicWeatherInterpolation.weatherProviders;

import weatherCollector.entities.Weather;

public interface WeatherProvider {

   float getLatitude();
   float getLongitude();
   float getElevation();
   Weather currentWeather();
}
