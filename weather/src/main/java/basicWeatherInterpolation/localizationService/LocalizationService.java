package basicWeatherInterpolation.localizationService;

import basicWeatherInterpolation.weatherProviders.*;

import java.util.ArrayList;
import java.util.List;

public class LocalizationService {

    LocalizationService(){
        this(3);
    }

    LocalizationService(int numOfClosestsProviders) {
        weatherProviders.add(new ImgwWeatherProvider("Kasprowy WIerch"));
        weatherProviders.add(new ImgwWeatherProvider("Zakopane"/*TODO: Coords of IMGW station, repeat for all stations*/));
        weatherProviders.add(new PogodynkaWeatherProvider(/*TODO: Coords of IMGW station, repeat for all stations*/));
        weatherProviders.add(new MountainForecastWeatherProvider(new MountainForecastPageParser(),PeakName.Rysy));
    }










    int numOfClosestsProviders = 1;
    private List<WeatherProvider> weatherProviders = new ArrayList<WeatherProvider>();
}
