package weatherCollector.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import weatherCollector.services.WeatherCollectorService;

import java.io.IOException;

@Controller
public class WeatherCollectorController {

    private static final Logger logger = LogManager.getLogger();
    @Autowired
    private WeatherCollectorService collector;

    /**method retrieves weather data from html and saves in db
     * curl -i -X GET http://localhost:8080/getWeatherData
     * @return ResponseEntity indicating operation result
     */
    @RequestMapping(name = "/getWeatherData", method = RequestMethod.GET)
    public ResponseEntity getWeather() {
        logger.info("Collecting weather data stared.");
        try {
            collector.collectWeatherData();
        } catch (IllegalStateException e) {
            logger.warn("Error while retrieving data from HTML.", e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            logger.warn("Error while retrieving data from HTML.", e);
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        logger.info("Collecting weather data finished.");
        return new ResponseEntity(HttpStatus.OK);
    }
}
