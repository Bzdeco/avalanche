package basicWeatherInterpolation.localizationService;


import basicWeatherInterpolation.weatherProviders.ImgwWeatherProvider;
import basicWeatherInterpolation.weatherProviders.PogodynkaWeatherProvider;
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

    public LocalizationService(){
        this(3);
    }

    public LocalizationService(int numOfClosestsProviders) {
        this.numOfClosestsProviders = numOfClosestsProviders;

        weatherProviders.add(new ImgwWeatherProvider("Kasprowy Wierch", "peak"));
        weatherProviders.add(new ImgwWeatherProvider("Zakopane", "city"));
        weatherProviders.add(new PogodynkaWeatherProvider("Morskie Oko", "lake"));
        weatherProviders.add(new PogodynkaWeatherProvider("Hala Gasienicowa", "valley"));
        weatherProviders.add(new PogodynkaWeatherProvider("Kasprowy Wierch", "peak"));
        weatherProviders.add(new PogodynkaWeatherProvider("Polana chocholowska", "valley"));
        weatherProviders.add(new PogodynkaWeatherProvider("Zakopane", "city"));
        weatherProviders.add(new PogodynkaWeatherProvider("Poronin", "city"));
        weatherProviders.add(new PogodynkaWeatherProvider("Bukowina Tatrzanska", "city"));
//        weatherProviders.add(new MountainForecastWeatherProvider(/*TODO: Name of MF station, repeat for all stations*/));


    }

    List <WeatherProvider> getClosestWeatherProviders(Coords interpolatedLocation){

        List<Pair<Double, WeatherProvider>> distanceToProvider = new ArrayList<>();
        for(WeatherProvider provider: weatherProviders){
            distanceToProvider.add(new Pair<>(interpolatedLocation.distance(provider.getCoordinates()), provider));
        }

        distanceToProvider.sort(Comparator.comparingDouble(Pair::getKey));

        List<WeatherProvider> closestProviders = new ArrayList<>(numOfClosestsProviders);
        for(int i = 0; i < numOfClosestsProviders; ++i){
            closestProviders.add(distanceToProvider.get(i).getValue());
        }

        return closestProviders;
    }
}
