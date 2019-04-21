package basicWeatherInterpolation.localizationService;

import basicWeatherInterpolation.weatherProviders.ImgwWeatherProvider;
import basicWeatherInterpolation.weatherProviders.WeatherProvider;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import weatherCollector.coordinates.Coords;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LocalizationService {

    @Setter
    @Getter
    private int numOfClosestsProviders;

    private List<WeatherProvider> weatherProviders = new ArrayList<>();
    private List<WeatherProvider> closestProviders;

    public LocalizationService(){
        this(3);
    }

    public LocalizationService(int numOfClosestsProviders) {
        this.numOfClosestsProviders = numOfClosestsProviders;

        weatherProviders.add(new ImgwWeatherProvider("Kasprowy Wierch"));
        weatherProviders.add(new ImgwWeatherProvider("Zakopane"));
//        weatherProviders.add(new PogodynkaWeatherProvider(/*TODO: Name of station, repeat for all stations*/));
//        weatherProviders.add(new MountainForecastWeatherProvider(/*TODO: Name of MF station, repeat for all stations*/));


    }
    //Constructor only for tests (dunno how to mock it better)
    public LocalizationService(List<WeatherProvider> mockProviders, int numOfClosestsProviders){
        weatherProviders = mockProviders;
        this.numOfClosestsProviders = numOfClosestsProviders;
    }


    List <WeatherProvider> getClosestWeatherProviders(Coords interpolatedLocation){

        List<Pair<Double, WeatherProvider>> distanceToProvider = new ArrayList<>();
        for(WeatherProvider provider: weatherProviders){
            distanceToProvider.add(new Pair<>(interpolatedLocation.distance(provider.getCoordinates()), provider));
        }

        distanceToProvider.sort(Comparator.comparingDouble(Pair::getKey));

        closestProviders = new ArrayList<>(numOfClosestsProviders);
        for(int i = 0; i < numOfClosestsProviders; ++i){
            closestProviders.add(distanceToProvider.get(i).getValue());
        }

        return closestProviders;
    }
}
