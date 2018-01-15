package weatherCollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class WeatherApplication {

	public static void main(String[] args) throws IOException {
	    //System.setProperty("filename", "M-34-101-A-b-3-3-1.las"); // TODO remove
        //System.out.println("filename: "+System.getProperty("filename"));
        SpringApplication.run(WeatherApplication.class, args);
	}
}
