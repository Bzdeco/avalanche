package weatherCollector.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weatherCollector.entities.Weather;
import weatherCollector.parser.dto.Measurement;
import weatherCollector.parser.dto.PrecipitationM;
import weatherCollector.parser.dto.TempM;
import weatherCollector.parser.dto.WindM;
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
        List<Measurement> t = parser.getTemperature();
        List<Measurement> w = parser.getWind();
        List<Measurement> p = parser.getPrecipitation();

        if (t.size() != w.size() || t.size() != p.size())
            throw new IllegalStateException("Lists' size is not equal.");

        for (int i = 0; i < t.size(); i++) {
            Weather weather = new Weather((TempM) t.get(i), (WindM) w.get(i), (PrecipitationM) p.get(i));
            Weather found = weatherRepo.findOne(weather.getTime());
            if (found == null)
                weatherRepo.save(weather);
        }
    }
}