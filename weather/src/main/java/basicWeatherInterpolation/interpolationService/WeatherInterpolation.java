package basicWeatherInterpolation.interpolationService;

import basicWeatherInterpolation.localizationService.LocalizationService;
import basicWeatherInterpolation.weatherProviders.ImgwWeatherProvider;
import basicWeatherInterpolation.weatherProviders.MountainForecastWeatherProvider;
import basicWeatherInterpolation.weatherProviders.PogodynkaWeatherProvider;
import basicWeatherInterpolation.weatherProviders.WeatherProvider;
import weatherCollector.coordinates.Coords;
import weatherCollector.entities.Weather;

import java.beans.Statement;
import java.util.List;


public class WeatherInterpolation {

    public Weather getInterpolatedCurrentWeather(Coords interpolatedLocation,int  numberOfClosestProviders){

        LocalizationService localizationService = new LocalizationService(numberOfClosestProviders);
        List<WeatherProvider> providers = localizationService.getClosestWeatherProviders(interpolatedLocation);

        Weather interpolatedCurrentWeather = null;
        try {
            interpolatedCurrentWeather = providers.get(0).currentWeather();
            for (WeatherProvider provider : providers.subList(1, providers.size()))
                interpolatedCurrentWeather = interpolateWeatherParams(interpolatedCurrentWeather, provider);
        } catch (Exception ignored){}

        return interpolatedCurrentWeather;
    }

    private Weather interpolateWeatherParams(Weather cw, WeatherProvider provider) throws Exception {

        String[] imgwFieldsToSet = {"Temp", "WindSpeed", "WindDeg", "Humidity", "Rain", "Snow"};
        String[] pogodynkaFieldsToSet = {"Temp", "TempMax", "TempMin", "WindSpeed", "WindDeg", "Snow"};
        String[] mountainForecastFieldsToSet = {"Temp", "WindSpeed", "WindDeg", "Rain", "Snow", "TempMax", "TempMin", "SeaLevel"};

        String[] fields = {};
        if(provider instanceof ImgwWeatherProvider) fields = imgwFieldsToSet;
        if(provider instanceof PogodynkaWeatherProvider) fields = pogodynkaFieldsToSet;
        if(provider instanceof MountainForecastWeatherProvider) fields = mountainForecastFieldsToSet;


        Weather weather = provider.currentWeather();
        Statement statement;

        for (String field : fields) {
            Object weatherParam = weather.getClass().getMethod("get" + field).invoke(weather);
            Object cwParam = cw.getClass().getMethod("get" + field).invoke(cw);

            if (weatherParam != null)
                if (cwParam == null) {
                    statement = new Statement(cw, "set" + field, new Float[]{(Float) weatherParam});
                    statement.execute();
                } else {
                    statement = new Statement(cw, "set" + field, new Float[]{((Float) weatherParam + (Float)cwParam) / 2});
                    statement.execute();

                }
        }
        return cw;
    }

}
