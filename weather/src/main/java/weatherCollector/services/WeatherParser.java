package weatherCollector.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import weatherCollector.parser.dto.CloudMeasurement;
import weatherCollector.parser.dto.Measurement;
import weatherCollector.parser.dto.Measurements;
import weatherCollector.parser.dto.PrecipitationMeasurement;
import weatherCollector.parser.dto.SnowMeasurement;
import weatherCollector.parser.dto.TempMeasurement;
import weatherCollector.parser.dto.WindMeasurement;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class WeatherParser
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String BASE_URL = "http://www.weatheronline.pl/weather/maps/city?";

    public Measurements getMeasurements()
    {
        LOGGER.info("Getting data about temperature.");
        final List<Measurement> temperatureMeasurementList = getMeasurement(new TempMeasurement());
        LOGGER.info("Getting data about wind.");
        final List<Measurement> windMeasurementList = getMeasurement(new WindMeasurement());
        LOGGER.info("Getting data about precipitation.");
        final List<Measurement> precipitationMeasurementList = getMeasurement(new PrecipitationMeasurement());
        LOGGER.info("Getting data about cloudiness.");
        final List<Measurement> cloudMeasurementList = getMeasurement(new CloudMeasurement());
        LOGGER.info("Getting data about snow level.");
        final List<Measurement> snowMeasurementList = getMeasurement(new SnowMeasurement());

        return Measurements.constructFromLists(
                temperatureMeasurementList,
                windMeasurementList,
                precipitationMeasurementList,
                cloudMeasurementList,
                snowMeasurementList);
    }

    private List<Measurement> getMeasurement(Measurement measurement)
    {
        Document doc = obtainDocument(measurement);
        Elements elements = doc.select(measurement.getFilter());
        List<Measurement> measurements = new ArrayList<>();
        for (Element el : elements) {
            try {
                Measurement m = measurement.fromElement(el);
                measurements.add(m.fromElement(el));
            } catch (ParseException e) {
                e.printStackTrace();
                LOGGER.warn("Error while parsing date.", e);
            }
        }
        LOGGER.info("Received {} items.", measurements.size());
        return measurements;
    }

    private Document obtainDocument(final Measurement measurement)
    {
        final String url = BASE_URL + measurement.getURL();
        try {
            return Jsoup.connect(url).timeout(5000).get();
        } catch (IOException ex) {
            LOGGER.error(String.format("Couldn't obtain or parse HTML from %s", url), ex);
            throw new UncheckedIOException(ex);
        }
    }

}
