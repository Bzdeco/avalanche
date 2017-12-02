package weatherCollector.parser.dto;

import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class WeatherMeasurement
{
    private final CloudMeasurement cloudMeasurement;
    private final PrecipitationMeasurement precipitationMeasurement;
    private final SnowMeasurement snowMeasurement;
    private final TemperatureMeasurement temperatureMeasurement;
    private final WindMeasurement windMeasurement;

    public static WeatherMeasurement constructWeatherMeasurement(final CloudMeasurement cloudMeasurement,
                                                                 final PrecipitationMeasurement precipitationMeasurement,
                                                                 final SnowMeasurement snowMeasurement,
                                                                 final TemperatureMeasurement temperatureMeasurement,
                                                                 final WindMeasurement windMeasurement)
    {
        checkNotNull(cloudMeasurement);
        checkNotNull(precipitationMeasurement);
        checkNotNull(snowMeasurement);
        checkNotNull(temperatureMeasurement);
        checkNotNull(windMeasurement);

        checkState(isAllMeasurementsFromSameTime(
                cloudMeasurement,
                precipitationMeasurement,
                snowMeasurement,
                temperatureMeasurement,
                windMeasurement));

        return new WeatherMeasurement(cloudMeasurement,
                precipitationMeasurement,
                snowMeasurement,
                temperatureMeasurement,
                windMeasurement);
    }

    private static boolean isAllMeasurementsFromSameTime(final CloudMeasurement cloudMeasurement,
                                                         final PrecipitationMeasurement precipitationMeasurement,
                                                         final SnowMeasurement snowMeasurement,
                                                         final TemperatureMeasurement temperatureMeasurement,
                                                         final WindMeasurement windMeasurement)
    {
        return Stream.of(cloudMeasurement,
                precipitationMeasurement,
                snowMeasurement,
                temperatureMeasurement,
                windMeasurement)
                .allMatch(measurement -> measurement.getTime() == cloudMeasurement.getTime());
    }

    private static void validate(final CloudMeasurement cloudMeasurement,
                                 final PrecipitationMeasurement precipitationMeasurement,
                                 final SnowMeasurement snowMeasurement,
                                 final TemperatureMeasurement temperatureMeasurement,
                                 final WindMeasurement windMeasurement)
    {

    }

    private WeatherMeasurement(final CloudMeasurement cloudMeasurement,
                              final PrecipitationMeasurement precipitationMeasurement,
                              final SnowMeasurement snowMeasurement,
                              final TemperatureMeasurement temperatureMeasurement,
                              final WindMeasurement windMeasurement)
    {
        this.cloudMeasurement = cloudMeasurement;
        this.precipitationMeasurement = precipitationMeasurement;
        this.snowMeasurement = snowMeasurement;
        this.temperatureMeasurement = temperatureMeasurement;
        this.windMeasurement = windMeasurement;
    }

    public CloudMeasurement getCloudMeasurement()
    {
        return cloudMeasurement;
    }

    public PrecipitationMeasurement getPrecipitationMeasurement()
    {
        return precipitationMeasurement;
    }

    public SnowMeasurement getSnowMeasurement()
    {
        return snowMeasurement;
    }

    public TemperatureMeasurement getTemperatureMeasurement()
    {
        return temperatureMeasurement;
    }

    public WindMeasurement getWindMeasurement()
    {
        return windMeasurement;
    }
}
