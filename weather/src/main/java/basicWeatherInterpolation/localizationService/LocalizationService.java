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
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.BANIKOV));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.BARANEC));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.GUBALOWKA));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.CHOPOK));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.DERESE));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.DUMBIER));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.GERLACH));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.GIEWONT));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.KASPROWY_WIERCH));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.KOSCIELEC));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.KRIVAN));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.MNICH));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.OSTRY_ROHAC));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.RYSY));
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
