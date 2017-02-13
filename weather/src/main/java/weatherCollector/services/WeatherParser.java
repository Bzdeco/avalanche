package weatherCollector.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import weatherCollector.parser.dto.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class WeatherParser {
    private static final Logger logger = LogManager.getLogger();
    private static final String baseURL = "http://www.weatheronline.pl/weather/maps/city?";

    List<Measurement> getTemperature() throws IOException {
        logger.info("Getting data about temperature.");
        return getMeasurement(new TempM());
    }

    List<Measurement> getWind() throws IOException {
        logger.info("Getting data about wind.");
        return getMeasurement(new WindM());
    }

    List<Measurement> getPrecipitation() throws IOException {
        logger.info("Getting data about precipitation.");
        return getMeasurement(new PrecipitationM());
    }

    List<Measurement> getClouds() throws IOException {
        logger.info("Getting data about cloudiness.");
        return getMeasurement(new CloudsM());
    }

    List<Measurement> getSnow() throws IOException {
        logger.info("Getting data about snow level.");
        return getMeasurement(new SnowM());
    }

    private <T extends Measurement> List<Measurement> getMeasurement(T measurementType) throws IOException {
        Document doc = Jsoup.connect(baseURL + measurementType.getURL()).timeout(5000).get();
        org.jsoup.select.Elements elements = doc.select(measurementType.getFilter());
        List<Measurement> measurements = new ArrayList<>();
        for (Element el : elements) {
            try {
                Measurement m = measurementType.fromElement(el);
                measurements.add(m.fromElement(el));
            } catch (ParseException e) {
                e.printStackTrace();
                logger.warn("Error while parsing date.", e);
            }
        }
        logger.info("Received {} items.", measurements.size());
        return measurements;
    }

}
