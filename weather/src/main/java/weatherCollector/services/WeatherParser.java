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
import weatherCollector.parser.dto.PrecipitationMeasurement;
import weatherCollector.parser.dto.SnowMeasurement;
import weatherCollector.parser.dto.TemperatureMeasurement;
import weatherCollector.parser.dto.WeatherMeasurement;
import weatherCollector.parser.dto.WindMeasurement;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;

@Component
public class WeatherParser
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String BASE_URL = "http://www.weatheronline.pl/weather/maps/city?";

    public List<WeatherMeasurement> getMeasurements()
    {
        LOGGER.info("Getting data about temperature.");
        final List<Measurement> temperatureMeasurementList = getMeasurement(new TemperatureMeasurement());
        LOGGER.info("Getting data about wind.");
        final List<Measurement> windMeasurementList = getMeasurement(new WindMeasurement());
        LOGGER.info("Getting data about precipitation.");
        final List<Measurement> precipitationMeasurementList = getMeasurement(new PrecipitationMeasurement());
        LOGGER.info("Getting data about cloudiness.");
        final List<Measurement> cloudMeasurementList = getMeasurement(new CloudMeasurement());
        LOGGER.info("Getting data about snow level.");
        final List<Measurement> snowMeasurementList = getMeasurement(new SnowMeasurement());

        checkState(isAllListsEqualInLength(
                cloudMeasurementList,
                precipitationMeasurementList,
                snowMeasurementList,
                temperatureMeasurementList,
                windMeasurementList
        ), "Downloaded measurements are invalid. Please restart application to redownload measurements.");

        final List<WeatherMeasurement> weatherMeasurements = new ArrayList<>();
        for (int i = 0; i < temperatureMeasurementList.size(); i++) {
            weatherMeasurements.add(WeatherMeasurement.constructWeatherMeasurement(
                    (CloudMeasurement) cloudMeasurementList.get(i),
                    (PrecipitationMeasurement) precipitationMeasurementList.get(i),
                    (SnowMeasurement) snowMeasurementList.get(i),
                    (TemperatureMeasurement) temperatureMeasurementList.get(i),
                    (WindMeasurement) windMeasurementList.get(i))
            );
        }
        return weatherMeasurements;
    }

    private boolean isAllListsEqualInLength(final List<Measurement> cloudMeasurementList,
                                            final List<Measurement> precipitationMeasurementList,
                                            final List<Measurement> snowMeasurementList,
                                            final List<Measurement> temperatureMeasurementList,
                                            final List<Measurement> windMeasurementList)
    {
        return Stream.of(
                cloudMeasurementList,
                precipitationMeasurementList,
                snowMeasurementList,
                temperatureMeasurementList,
                windMeasurementList)
                .allMatch(list -> list.size() == cloudMeasurementList.size());
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
