package backend.parsers.weatherHTML;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class App {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {

        WeatherParser weatherParser = new WeatherParser();
        try {
            List<Measurement> temp = weatherParser.getTemperature();
            List<Measurement> wind = weatherParser.getWind();
            List<Measurement> precipitation = weatherParser.getPrecipitation();
            System.out.println();
        } catch (IOException e) {
            logger.warn("Error while retrieving data from HTML.", e);
        }
    }


}
