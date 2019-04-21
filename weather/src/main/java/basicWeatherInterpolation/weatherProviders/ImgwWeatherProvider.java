package basicWeatherInterpolation.weatherProviders;

import com.fasterxml.jackson.databind.ObjectMapper;
import weatherCollector.coordinates.Coords;
import weatherCollector.entities.Weather;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ImgwWeatherProvider implements WeatherProvider{

    private String api_link = "https://danepubliczne.imgw.pl/api/data/synop/station/";
    private String fullLink;

    private Coords location;

    public ImgwWeatherProvider(String stationName){
        if(stationName == null || stationName.isEmpty())
            stationName = "Kasprowy Wierch";

        fullLink = api_link + parseName(stationName);
        //TODO: get latitude & longitude by name form web api


    }

    private String parseName(String stationName) {
        return stationName.toLowerCase().replaceAll("\\s", "");
    }

    @Override
    public Coords getCoordinates(){
        return location;
    }


    @Override
    public Weather currentWeather() throws IOException {
        URLConnection connection = new URL(fullLink).openConnection();
        connection.setRequestProperty("Accept-Charset", UTF_8.name());
        Scanner scanner = new Scanner(connection.getInputStream());
        String imgwResponse = scanner.useDelimiter("\\A").next();
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(imgwResponse, Weather.class);
    }
}
