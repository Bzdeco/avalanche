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
                .map(this::saveInRepository)
                .collect(Collectors.toList());
    }

    private boolean isNotInRepository(final Weather weather)
    {
        return weatherRepo.findOne(weather.getTime()) == null;
    }

    private Weather saveInRepository(final Weather weather)
    {
        weatherRepo.save(weather);
        return weather;
    }
}