package weatherCollector.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import weatherCollector.services.WeatherCollectorService;

import java.io.IOException;

@Controller
public class WeatherCollectorController {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private WeatherCollectorService collector;

    /**method retrieves weather data from html and saves in db
     * curl -i -X GET http://localhost:8080/getWeatherData
     * @return ResponseEntity indicating operation result
     */
    @RequestMapping(name = "/getWeatherData", method = RequestMethod.GET)
    public ResponseEntity getWeather(@RequestParam String filename) {
        LOGGER.info("Collecting weather data started for {}", filename);
        try {
            collector.collectWeatherData(filename);
        } catch (IOException e) {
            LOGGER.warn("Error while retrieving data.", e);
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Collecting weather data finished.");
        return new ResponseEntity(HttpStatus.OK);
    }
}