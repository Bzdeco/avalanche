package basicWeatherInterpolation.weatherProviders;

import weatherCollector.entities.Weather;


public class MountainForecastWeatherProvider implements WeatherProvider {

    private final MountainForecastPageParser.Peak peak;

    public MountainForecastWeatherProvider(MountainForecastPageParser parser, PeakName peakName) {
        this.peak = parser.getPeak(peakName);
    }

    @Override
    public float getLatitude() {
        return this.peak.getLatitude();
    }

    @Override
    public float getLongitude() {
        return this.peak.getLongitude();
    }

    @Override
    public float getElevation() {
        return this.peak.getHeight();
    }

    @Override
    public Weather currentWeather() {
       return this.peak.getWeather();
    }
}
