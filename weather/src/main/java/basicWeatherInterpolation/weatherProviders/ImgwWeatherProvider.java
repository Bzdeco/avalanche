package basicWeatherInterpolation.weatherProviders;

import weatherCollector.entities.Weather;

public class ImgwWeatherProvider implements WeatherProvider{
    public ImgwWeatherProvider(String stationName){}

    @Override
    public float getLatitude() {
        return 0;
    }

    @Override
    public float getLongitude() {
        return 0;
    }

    @Override
    public float getElevation() {
        return 0;
    }

    @Override
    public Weather currentWeather() {
        return null;
    }
}
