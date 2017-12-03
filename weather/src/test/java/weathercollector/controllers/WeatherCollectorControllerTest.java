package weathercollector.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import weathercollector.services.WeatherCollectorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WeatherCollectorControllerTest
{
    @Autowired
    private WeatherCollectorController collectorController;

    @MockBean
    private WeatherCollectorService collectorService;

    @Test
    public void shouldAlwaysCallCollectWeatherData() throws Exception
    {
        //given
        //when
        final ResponseEntity weather = collectorController.getWeather();
        //then
        verify(collectorService).collectWeatherData();
        assertThat(weather.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
