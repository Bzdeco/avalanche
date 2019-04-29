package basicWeatherInterpolation.weatherProviders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import weatherCollector.coordinates.Coords;
import weatherCollector.entities.Weather;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PogodynkaWeatherProvider implements WeatherProvider {

    private String stationName;
    private Coords location;

    public PogodynkaWeatherProvider(ProvidersName stationName, String locationType) {
        this.stationName = stationName.toString();

        try {
            location = retrieveCoordinates(this.stationName, locationType);
        } catch (IOException e) {
            // Set to center of Tatra Mountains
            location = new Coords(49.25f, 20f);
        }
    }

    /**
     * @param stationName name of the station to get coordinates
     * @param locationType type of the station to get coordinates
     * @return  coordinates od station
     * @throws IOException when can't get conection from OpenWeatherApi
     */
    private Coords retrieveCoordinates(String stationName, String locationType) throws IOException {
        URI url  = URI.create( "https://nominatim.openstreetmap.org/search?q="
                + stationName.replaceAll(" ", "+")
                + "+" + locationType + "&format=json");

        String locationResponse = getJsonResponse(url.toASCIIString());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(locationResponse).get(0);

        float longitude = Float.parseFloat(node.get("lon").asText());
        float latitude  = Float.parseFloat(node.get("lat").asText());

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
        String api_link = "http://www.pogodynka.pl/gory/pogoda/tatry";

        Document doc = Jsoup.connect(api_link).get();
        Elements table = doc.select(".gory_table").last().child(1).children();

        // Remove faulty elements from table, leave only children
        table.removeIf(elem -> elem.text().startsWith("<script"));

        String windDegIconNumber = "0";
        Weather result = new Weather();

        for (Element row : table.select("tr")) { //first row is the col names so skip it.
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
        } catch (NullPointerException e) {
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
            windSpeed = getAvgWindSpeedFromBoufortScale(Integer.parseInt(cols.get(7).text()));
        } catch (Exception e) {
            windSpeed = null;
        }
        try {
            windDeg = 45f * Float.parseFloat(windDegIconNumber);
        } catch (Exception e) {
            windDeg = null;
        }
        getCoordinates().setElevation(elevation);
        result.setTime(new Date());
        result.setTemp(temp);
        result.setTempMax(temp);
        result.setTempMin(temp);
        result.setWindSpeed(windSpeed);
        result.setWindDeg(windDeg);
        result.setSnow(freshSnowLvl);
    }

    private Float getAvgWindSpeedFromBoufortScale(Integer windSpeedBoufortScale) {
        float windSpeed;
        switch(windSpeedBoufortScale) {
            case 0:
                windSpeed = 0.1f;
                break;
            case 1:
                windSpeed = 0.9f;
                break;
            case 2:
                windSpeed = 2.5f;
                break;
            case 3:
                windSpeed = 4.5f;
                break;
            case 4:
                windSpeed = 6.7f;
                break;
            case 5:
                windSpeed = 9.35f;
                break;
            case 6:
                windSpeed = 12.3f;
                break;
            case 7:
                windSpeed = 15.5f;
                break;
            case 8:
                windSpeed = 18.95f;
                break;
            case 9:
                windSpeed = 22.6f;
                break;
            case 10:
                windSpeed = 26.45f;
                break;
            case 11:
                windSpeed = 30.55f;
                break;
            case 12:
                windSpeed = 40f;
                break;
            default:
                windSpeed = 0f;
        }
        return windSpeed;
    }
}


