package weatherCollector.parser.dto;

import weatherCollector.entities.Weather;

import java.util.List;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class Measurements
{
    private final List<Measurement> temperatureMeasurementList;
    private final List<Measurement> windMeasurementList;
    private final List<Measurement> precipitationMeasurementList;
    private final List<Measurement> cloudMeasurementList;
    private final List<Measurement> snowMeasurementList;

    public static Measurements constructFromLists(final List<Measurement> temperatureMeasurementList,
                                                  final List<Measurement> windMeasurementList,
                                                  final List<Measurement> precipitationMeasurementList,
                                                  final List<Measurement> cloudMeasurementList,
                                                  final List<Measurement> snowMeasurementList)
    {
        validateMeasurementsLists(
                temperatureMeasurementList,
                windMeasurementList,
                precipitationMeasurementList,
                cloudMeasurementList,
                snowMeasurementList);

        return new Measurements(
                temperatureMeasurementList,
                windMeasurementList,
                precipitationMeasurementList,
                cloudMeasurementList,
                snowMeasurementList);
    }

    private static void validateMeasurementsLists(final List<Measurement> temperatureMeasurementList,
                                                  final List<Measurement> windMeasurementList,
                                                  final List<Measurement> precipitationMeasurementList,
                                                  final List<Measurement> cloudMeasurementList,
                                                  final List<Measurement> snowMeasurementList)
    {
        checkNotNull(temperatureMeasurementList);
        checkNotNull(windMeasurementList);
        checkNotNull(precipitationMeasurementList);
        checkNotNull(cloudMeasurementList);
        checkNotNull(snowMeasurementList);

        checkState(isAllListsEqualInLength(
                temperatureMeasurementList,
                windMeasurementList,
                precipitationMeasurementList,
                cloudMeasurementList,
                snowMeasurementList
        ), "Measurements are of different size. Please download measurements again.");
    }

    private static boolean isAllListsEqualInLength(final List<Measurement> temperatureMeasurementList,
                                                   final List<Measurement> windMeasurementList,
                                                   final List<Measurement> precipitationMeasurementList,
                                                   final List<Measurement> cloudMeasurementList,
                                                   final List<Measurement> snowMeasurementList)
    {
        return Stream.of(temperatureMeasurementList,
                windMeasurementList,
                precipitationMeasurementList,
                cloudMeasurementList,
                snowMeasurementList)
                .map(List::size)
                .allMatch(size -> size == temperatureMeasurementList.size());
    }

    private Measurements(final List<Measurement> temperatureMeasurementList,
                         final List<Measurement> windMeasurementList,
                         final List<Measurement> precipitationMeasurementList,
                         final List<Measurement> cloudMeasurementList,
                         final List<Measurement> snowMeasurementList)
    {
        this.temperatureMeasurementList = temperatureMeasurementList;
        this.windMeasurementList = windMeasurementList;
        this.precipitationMeasurementList = precipitationMeasurementList;
        this.cloudMeasurementList = cloudMeasurementList;
        this.snowMeasurementList = snowMeasurementList;
    }

    public List<Measurement> getTemperatureMeasurementList()
    {
        return temperatureMeasurementList;
    }

    public List<Measurement> getWindMeasurementList()
    {
        return windMeasurementList;
    }

    public List<Measurement> getPrecipitationMeasurementList()
    {
        return precipitationMeasurementList;
    }

    public List<Measurement> getCloudMeasurementList()
    {
        return cloudMeasurementList;
    }

    public List<Measurement> getSnowMeasurementList()
    {
        return snowMeasurementList;
    }

    public Weather buildWeatherFromMeasurement(int position)
    {
        return new Weather(
                (TempMeasurement) temperatureMeasurementList.get(position),
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
