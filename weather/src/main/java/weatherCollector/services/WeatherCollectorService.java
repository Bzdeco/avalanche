package weatherCollector.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weatherCollector.entities.Weather;
import weatherCollector.parser.dto.Measurements;
import weatherCollector.repositories.WeatherRepository;

@Service
public class WeatherCollectorService
{

    @Autowired
    private WeatherParser parser;

    @Autowired
    private WeatherRepository weatherRepo;

    public void collectWeatherData()
    {
        final Measurements measurements = parser.getMeasurements();

        for (int i = 0; i < measurements.measurementCount(); i++) {
            final Weather weather = measurements.buildWeatherFromMeasurement(i);
            saveInDatabaseIfNotExists(weather);
        }
    }

    private void saveInDatabaseIfNotExists(final Weather weather)
    {
        if (weatherRepo.findOne(weather.getTime()) == null) {
            weatherRepo.save(weather);
        }
    }
}