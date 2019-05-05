package basicWeatherInterpolation.weatherProviders;

import weatherCollector.coordinates.Coords;
import weatherCollector.entities.Weather;


public class MountainForecastWeatherProvider implements WeatherProvider {

    private final Peak peak;

    public MountainForecastWeatherProvider(MountainForecastPageParser parser, PeakName peakName) {
        this.peak = parser.getPeak(peakName);
    }

    @Override
    public Coords getCoordinates() {
        Coords coords = new Coords(peak.getLatitude(),peak.getLongitude());
        coords.setElevation(peak.getHeight());
        return coords;
    }

    @Override
    public Weather currentWeather() {
       return this.peak.getWeather();
    }
}
