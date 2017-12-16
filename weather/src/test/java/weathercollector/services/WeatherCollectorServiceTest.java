package weathercollector.services;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import weathercollector.entities.Weather;
import weathercollector.measurements.CloudMeasurement;
import weathercollector.measurements.PrecipitationMeasurement;
import weathercollector.measurements.SnowMeasurement;
import weathercollector.measurements.TemperatureMeasurement;
import weathercollector.measurements.WeatherMeasurement;
import weathercollector.measurements.WindMeasurement;
import weathercollector.repositories.WeatherRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WeatherCollectorServiceTest
{
    private static WeatherMeasurement DUMMY_WEATHER_MEASUREMENT = getDummyWeatherMeasurement();

    @MockBean
    private WeatherParser weatherParser;

    @MockBean
    private WeatherRepository weatherRepository;

    @Autowired
    private WeatherCollectorService weatherCollectorService;

    @Test
    public void shouldSaveResultInRepo() throws Exception
    {
        //given
        given(weatherParser.getMeasurements()).willReturn(ImmutableList.of(DUMMY_WEATHER_MEASUREMENT));
        //when
        final List<Weather> weatherList = weatherCollectorService.collectWeatherData();
        //then
        assertThat(weatherList)
                .isNotNull()
                .hasSize(1);
        assertThat(weatherList.get(0))
                .isEqualToComparingFieldByField(new Weather(DUMMY_WEATHER_MEASUREMENT));
        verify(weatherParser).getMeasurements();
        verify(weatherRepository).save(any(Weather.class));
    }

    @Test
    public void shouldNotSaveInRepo() throws Exception
    {
        //given
        given(weatherParser.getMeasurements()).willReturn(ImmutableList.of());
        //when
        final List<Weather> weatherList = weatherCollectorService.collectWeatherData();
        //then
        assertThat(weatherList)
                .isNotNull()
                .isEmpty();
        verify(weatherParser).getMeasurements();
        verify(weatherRepository, times(0)).save(any(Weather.class));
    }

    private static WeatherMeasurement getDummyWeatherMeasurement()
    {
        final WeatherMeasurement mock = mock(WeatherMeasurement.class, withSettings().stubOnly());
        given(mock.getCloudMeasurement()).willReturn(new CloudMeasurement());
        given(mock.getPrecipitationMeasurement()).willReturn(new PrecipitationMeasurement());
        given(mock.getSnowMeasurement()).willReturn(new SnowMeasurement());
        given(mock.getTemperatureMeasurement()).willReturn(new TemperatureMeasurement());
        given(mock.getWindMeasurement()).willReturn(new WindMeasurement());
        return mock;
    }
}
