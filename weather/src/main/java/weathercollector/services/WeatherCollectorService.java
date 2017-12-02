package weathercollector.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weathercollector.entities.Weather;
import weathercollector.repositories.WeatherRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeatherCollectorService
{
    @Autowired
    private WeatherParser parser;

    @Autowired
    private WeatherRepository weatherRepo;

    public List<Weather> collectWeatherData()
    {
        return parser.getMeasurements()
                .stream()
                .map(Weather::new)
                .filter(this::isNotInRepository)
                .map(weather -> weatherRepo.save(weather))
                .collect(Collectors.toList());
    }

    private boolean isNotInRepository(final Weather weather)
    {
        return weatherRepo.findOne(weather.getTime()) == null;
    }
}