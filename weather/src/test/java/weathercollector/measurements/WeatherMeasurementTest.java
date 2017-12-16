package weathercollector.measurements;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Date;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class WeatherMeasurementTest
{
    @Mock
    private CloudMeasurement cloudMeasurement;

    @Mock
    private PrecipitationMeasurement precipitationMeasurement;

    @Mock
    private SnowMeasurement snowMeasurement;

    @Mock
    private TemperatureMeasurement temperatureMeasurement;

    @Mock
    private WindMeasurement windMeasurement;

    @Test
    public void shouldConstructWeatherMeasurement() throws Exception
    {
        //given
        //when
        final Throwable throwable = catchThrowable(() -> WeatherMeasurement.constructWeatherMeasurement(
                cloudMeasurement,
                precipitationMeasurement,
                snowMeasurement,
                temperatureMeasurement,
                windMeasurement
        ));
        //then
        assertThat(throwable).doesNotThrowAnyException();
    }

    @Test
    public void shouldFailToConstructWeatherMeasurementDueToDifferentMeasurementTimes() throws Exception
    {
        //given
        given(cloudMeasurement.getTime()).willReturn(Date.from(Instant.now()));
        //when
        final Throwable throwable = catchThrowable(() -> WeatherMeasurement.constructWeatherMeasurement(
                cloudMeasurement,
                precipitationMeasurement,
                snowMeasurement,
                temperatureMeasurement,
                windMeasurement
        ));
        //then
        assertThat(throwable)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Measurements should have the same time. Redownload measurements.");
    }
}
