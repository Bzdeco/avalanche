package org.avalanche.weather.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.avalanche.weather.coordinates.Coords;
import org.avalanche.weather.entities.Weather;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ImgwWeatherProvider implements WeatherProvider {

    private static final String API_LINK = "https://danepubliczne.imgw.pl/api/data/synop/station/";
    private Coords location;
    private static final float DEFAULT_LATITUDE = 49.25f;
    private static final float DEFAULT_LONGITUDE = 20f;
    private String STATION_API_LINK;

    public ImgwWeatherProvider(ProvidersName stationName, LocationType locationType){
        STATION_API_LINK = API_LINK + parseName(stationName.toString()) + "/format/json";

        try {
            location = retrieveCoordinates(stationName.toString(), locationType.toString());
        } catch (IOException ignored) {
            //Set to center of Tatra Mountains
            location = new Coords(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
        }
    }

    public ImgwWeatherProvider(String stationName, LocationType locationType) {
        STATION_API_LINK = API_LINK + parseName(stationName) + "/format/json";

        try {
            location = retrieveCoordinates(stationName, locationType.toString());
        } catch (IOException ignored) {
            //Set to center of Tatra Mountains
            location = new Coords(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
        }
    }

    /**
     * @param stationName name of the station to get coordinates
     * @param locationType type of the station to get coordinates
     * @return  coordinates od station
     * @throws IOException when can't get conection from OpenWeatherApi
     */
    private Coords retrieveCoordinates(String stationName, String locationType) throws IOException {
        String url  =  "https://nominatim.openstreetmap.org/search?q="
                + stationName.replaceAll(" ", "+")
                + "+" + locationType + "&format=json";

        String locationResponse = getJsonResponse(url);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(locationResponse).get(0);

        float longitude = Float.parseFloat(node.get("lon").asText());
        float latitude  = Float.parseFloat(node.get("lat").asText());

        return new Coords(latitude, longitude);
    }

    /**
     * Parses @param stationName to query for IMGW AIP
     * @return parsed name
     */
    private String parseName(String stationName) {
        return stationName.toLowerCase().replaceAll("\\s", "");
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

    /**
     * @return current Weather retrieved from IMGP API
     * @throws IOException when can't get response from IMGW API
     */
    @Override
    public Weather currentWeather() throws IOException {
        Weather weather = new Weather();
        String imgwResponse = getJsonResponse(STATION_API_LINK);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(imgwResponse);

        weather.setTime(parseDate(json));
        weather.setTemp(Float.parseFloat(json.get("temperatura").asText()));
        weather.setWindSpeed(Float.parseFloat(json.get("predkosc_wiatru").asText()));
        weather.setWindDeg(Float.parseFloat(json.get("kierunek_wiatru").asText()));
        weather.setHumidity(Float.parseFloat(json.get("wilgotnosc_wzgledna").asText()));
        if( weather.getTemp() > 0)
            weather.setRain(Float.parseFloat(json.get("suma_opadu").asText()));
        else
            weather.setSnow(Float.parseFloat(json.get("suma_opadu").asText()));

        return weather;
    }


    /**
     * @param json from IMGW
     * @return Date retrieved from IMGW response JSON
     */
    private LocalDateTime parseDate(JsonNode json) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss");
        return LocalDateTime.parse(json.get("data_pomiaru").asText() + ", " + json.get("godzina_pomiaru").asText() + ":00:00", formatter);
    }

    /**
     * @return coordinates of this weather station
     */
    @Override
    public Coords getCoordinates(){
        return location;
    }

    @Override
    public boolean equals(final Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        String other_fullLink = ((ImgwWeatherProvider) o).STATION_API_LINK;
        return STATION_API_LINK.equals(other_fullLink);
    }

    @Override
    public int hashCode(){
        return Objects.hash(STATION_API_LINK, location);
    }
}
