package basicWeatherInterpolation.localizationService;


import basicWeatherInterpolation.weatherProviders.*;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import weatherCollector.coordinates.Coords;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LocalizationService {

    @Setter
    @Getter
    private int numOfClosestProviders;

    private List<WeatherProvider> weatherProviders = new ArrayList<>();

    public LocalizationService(){
        this(3);
    }

    public LocalizationService(int numOfClosestProviders) {
        this.numOfClosestProviders = numOfClosestProviders;
        MountainForecastPageParser parser = new MountainForecastPageParser();

        weatherProviders.add(new ImgwWeatherProvider(     ProvidersName.KASPROWY_WIERCH,     LocationType.PEAK));
        weatherProviders.add(new ImgwWeatherProvider(     ProvidersName.ZAKOPANE,           LocationType.CITY));
        weatherProviders.add(new PogodynkaWeatherProvider(ProvidersName.MORSKIE_OKO,         LocationType.LAKE));
        weatherProviders.add(new PogodynkaWeatherProvider(ProvidersName.HALA_GASIENICOWA,    LocationType.EMPTY));
        weatherProviders.add(new PogodynkaWeatherProvider(ProvidersName.KASPROWY_WIERCH,     LocationType.PEAK));
        weatherProviders.add(new PogodynkaWeatherProvider(ProvidersName.POLANA_CHOCHOLOWSKA, LocationType.EMPTY));
        weatherProviders.add(new PogodynkaWeatherProvider(ProvidersName.DOLINA_PIECIU_STAWOW, LocationType.EMPTY));
        weatherProviders.add(new PogodynkaWeatherProvider(ProvidersName.ZAKOPANE,           LocationType.CITY));
        weatherProviders.add(new PogodynkaWeatherProvider(ProvidersName.PORONIN,            LocationType.CITY));
        weatherProviders.add(new PogodynkaWeatherProvider(ProvidersName.BUKOWINA_TATRZANSKA, LocationType.CITY));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.Banikov ));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.Baranec ));
        weatherProviders.add(new MountainForecastWeatherProvider(parser, PeakName.Bubalowka ));
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

    public LocalizationService(int numOfClosestProviders, List<WeatherProvider> weatherProviders) {
        this.numOfClosestProviders = numOfClosestProviders;
        this.weatherProviders = weatherProviders;
    }

    List <WeatherProvider> getClosestWeatherProviders(Coords interpolatedLocation){
        return weatherProviders.stream()
                .map(provider -> new Pair<>(provider, interpolatedLocation.distance(provider.getCoordinates())))
                .sorted(Comparator.comparingDouble(Pair::getValue))
                .limit(numOfClosestProviders)
                .map(Pair::getKey)
                .collect(Collectors.toList());
    }
}
