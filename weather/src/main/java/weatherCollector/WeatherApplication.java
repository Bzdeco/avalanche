package weatherCollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import weatherCollector.services.WeatherCollectorService;

import java.io.IOException;

@SpringBootApplication
public class WeatherApplication {

	public static void main(String[] args) throws IOException {
		WeatherCollectorService collector = new WeatherCollectorService();
		//collector.collectWeatherData();
		SpringApplication.run(WeatherApplication.class, args);
	}
}
