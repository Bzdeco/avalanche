package basicWeatherInterpolation.weatherProviders;

import weatherCollector.coordinates.Coords;
import weatherCollector.entities.Weather;

public class ImgwWeatherProvider implements WeatherProvider{
    public ImgwWeatherProvider(String stationName){}
    
    @Override
    public Coords getCoordinates() {
        return null;
    }

    @Override
    public Weather currentWeather() {
        return null;
    }
}
