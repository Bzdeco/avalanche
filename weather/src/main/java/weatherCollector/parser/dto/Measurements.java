package weatherCollector.parser.dto;

import weatherCollector.entities.Weather;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class Measurements
{
    private final List<WeatherMeasurement> weatherMeasurementList;

    public Measurements(final List<WeatherMeasurement> weatherMeasurementList)
    {
        this.weatherMeasurementList = checkNotNull(weatherMeasurementList);
    }

    public Weather buildWeatherFromMeasurement(int position)
    {
        return new Weather(
                (TemperatureMeasurement) temperatureMeasurementList.get(position),
                (WindMeasurement) windMeasurementList.get(position),
                (PrecipitationMeasurement) precipitationMeasurementList.get(position),
                (CloudMeasurement) cloudMeasurementList.get(position),
                (SnowMeasurement) snowMeasurementList.get(position)
        );
    }

    public int measurementCount()
    {
        return temperatureMeasurementList.size();
    }
}
