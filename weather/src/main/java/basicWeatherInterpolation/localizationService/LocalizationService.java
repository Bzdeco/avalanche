package basicWeatherInterpolation.localizationService;


import basicWeatherInterpolation.weatherProviders.*;
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
        MountainForecastPageParser parser = new MountainForecastPageParser();

        weatherProviders.add(new ImgwWeatherProvider(ProvidersName.KasprowyWierch, "peak"));
        weatherProviders.add(new ImgwWeatherProvider(ProvidersName.Zakopane, "city"));
        weatherProviders.add(new PogodynkaWeatherProvider(ProvidersName.MorskieOko, "lake"));
        weatherProviders.add(new PogodynkaWeatherProvider(ProvidersName.HaleGasienicowa, ""));
        weatherProviders.add(new PogodynkaWeatherProvider(ProvidersName.KasprowyWierch, "peak"));
        weatherProviders.add(new PogodynkaWeatherProvider(ProvidersName.PolanaChocholowska, ""));
        weatherProviders.add(new PogodynkaWeatherProvider(ProvidersName.DolinaPieciuStawow, ""));
        weatherProviders.add(new PogodynkaWeatherProvider(ProvidersName.Zakopane, "city"));
        weatherProviders.add(new PogodynkaWeatherProvider(ProvidersName.Poronin, "city"));
        weatherProviders.add(new PogodynkaWeatherProvider(ProvidersName.BukowinaTatrzanska, "city"));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.Banikov ));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.Baranec ));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.Gubalowka));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.Chopok ));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.Derese ));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.Dumbier ));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.Gerlach ));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.Giewont ));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.KasprowyWierch ));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.Koscielec ));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.Krivan));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.Mnich));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.OstryRohac));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.Rysy));
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
