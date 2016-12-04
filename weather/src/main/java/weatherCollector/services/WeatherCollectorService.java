package weatherCollector.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weatherCollector.entities.Weather;
import weatherCollector.parser.dto.*;
import weatherCollector.repositories.WeatherRepository;

import java.io.IOException;
import java.util.List;

@Service
public class WeatherCollectorService {
    @Autowired
    private WeatherParser parser;

    @Autowired
    private WeatherRepository weatherRepo;

    public void collectWeatherData() throws IOException {
        List<Measurement> temp = parser.getTemperature();
        List<Measurement> wind = parser.getWind();
        List<Measurement> precip = parser.getPrecipitation();
        List<Measurement> clouds = parser.getClouds();
        List<Measurement> snow = parser.getSnow();

        if (temp.size() != wind.size() || temp.size() != precip.size()
                || temp.size() != clouds.size() || temp.size() != snow.size())
            throw new IllegalStateException("Lists' size is not equal.");

        for (int i = 0; i < temp.size(); i++) {
            Weather weather = new Weather((TempM) temp.get(i), (WindM) wind.get(i),
                    (PrecipitationM) precip.get(i),(CloudsM) clouds.get(i),(SnowM) snow.get(i));
            Weather found = weatherRepo.findOne(weather.getTime());
            if (found == null)
                weatherRepo.save(weather);
        }
    }
}