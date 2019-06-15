package org.avalanche.weather.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.avalanche.weather.coordinates.Coords;
import org.avalanche.weather.entities.Weather;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PogodynkaWeatherProvider implements WeatherProvider {

    private String stationName;
    private Coords location;
    private static final String API_LINK = "http://www.pogodynka.pl/gory/pogoda/tatry";
    private static final List<Float> BEAUFORT_TO_AVG_WIND_SPEED = Arrays.asList(0.1f, 0.9f, 2.5f, 4.5f, 6.7f, 9.35f, 12.3f, 15.5f, 18.95f, 22.6f, 26.45f, 30.55f, 40f);
    private static final float DEFAULT_LATITUDE = 49.25f;
    private static final float DEFAULT_LONGITUDE = 20f;

    public PogodynkaWeatherProvider(ProvidersName stationName, LocationType locationType) {
        this.stationName = stationName.toString();

        try {
            location = retrieveCoordinates(this.stationName, locationType.toString());
        } catch (IOException e) {
            // Set to center of Tatra Mountains
            location = new Coords(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
        }
    }

    /**
     * @param stationName  name of the station to get coordinates
     * @param locationType type of the station to get coordinates
     * @return coordinates od station
     * @throws IOException when can't get conection from OpenWeatherApi
     */
    private Coords retrieveCoordinates(String stationName, String locationType) throws IOException {
        URI url = URI.create("https://nominatim.openstreetmap.org/search?q="
                + stationName.replaceAll(" ", "+")
                + "+" + locationType + "&format=json");

        String locationResponse = getJsonResponse(url.toASCIIString());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(locationResponse).get(0);

        float longitude = Float.parseFloat(node.get("lon").asText());
        float latitude = Float.parseFloat(node.get("lat").asText());

        return new Coords(latitude, longitude);
    }

    /**
     * @param url from where get JSON
     * @return JSON response from given
     * @throws IOException when can't get response from IMGW API
     */
    private String getJsonResponse(String url) throws IOException {
        URLConnection connection = new URL(url).openConnection();
        connection.setRequestProperty("Accept-Charset", UTF_8.name());
        Scanner scanner = new Scanner(connection.getInputStream()).useDelimiter("\\A");
        return scanner.next();
    }

    @Override
    public Coords getCoordinates() {
        return location;
    }

    @Override
    public Weather currentWeather() throws IOException {
        Document doc = Jsoup.connect(API_LINK).get();
        Elements table = doc.select(".gory_table").last().child(1).children();

        // Remove faulty elements from table, leave only children
        table.removeIf(elem -> elem.text().startsWith("<script"));
        Weather result = new Weather();

        for (Element row : table.select("tr")) {
            Elements cols = row.select("td");
            if (cols.get(0).text().equals(stationName)) {
                setWeatherParams(result, cols);
                break;
            }
        }

        return result;
    }

    private void setWeatherParams(Weather result, Elements cols) {
        String windDegIconNumber;
        String tmp = cols.get(7).children().get(0).attributes().get("src");
        windDegIconNumber = tmp.substring(tmp.length() - 5, tmp.length() - 4);
        Float elevation;
        Float temp;
        Float freshSnowLvl;
        Float windSpeed;
        Float windDeg;

        try {
            elevation = Float.parseFloat(cols.get(1).text());
        } catch (Exception e) {
            elevation = null;
        }

        try {
            temp = Float.parseFloat(cols.get(2).text());
        } catch (Exception e) {
            temp = null;
        }
        try {
            freshSnowLvl = Float.parseFloat(cols.get(4).text());
        } catch (Exception e) {
            freshSnowLvl = null;
        }
        try {
            windSpeed = BEAUFORT_TO_AVG_WIND_SPEED.get(Integer.parseInt(cols.get(7).text()));
        } catch (Exception e) {
            windSpeed = null;
        }
        try {
            windDeg = 45f * Float.parseFloat(windDegIconNumber);
        } catch (Exception e) {
            windDeg = null;
        }
        getCoordinates().setElevation(elevation);
        result.setTime(LocalDateTime.now());
        result.setTemp(temp);
        result.setTempMax(temp);
        result.setTempMin(temp);
        result.setWindSpeed(windSpeed);
        result.setWindDeg(windDeg);
        result.setSnow(freshSnowLvl);
    }
}
