package weatherCollector.services;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weatherCollector.coordinates.Coords;
import weatherCollector.coordinates.StaticMapNameToCoordsConverter;
import weatherCollector.entities.Weather;
import weatherCollector.repositories.WeatherRepository;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class WeatherCollectorService {

    public final String API_KEY = "ab96126fe8504573743775d5d0665f78";
    public final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/forecast";
    public final StaticMapNameToCoordsConverter converter = new StaticMapNameToCoordsConverter();

    @Autowired
    private WeatherParser parser;

    @Autowired
    private WeatherRepository weatherRepo;

    public void collectWeatherData(String filename) throws IOException {
        String charset = UTF_8.name();
        final Coords coords = converter.convert(filename);

        String latitude = Float.toString(coords.getLatitude());
        String longitude = Float.toString(coords.getLongitude());

        String query = String.format("lat=%s&lon=%s&units=metric",
                URLEncoder.encode(latitude, charset),
                URLEncoder.encode(longitude, charset));

        URLConnection connection = new URL(WEATHER_URL + "?" + query).openConnection();
        connection.setRequestProperty("Accept-Charset", charset);
        connection.addRequestProperty("X-Api-Key", API_KEY);
        InputStream response = connection.getInputStream();
        Scanner scanner = new Scanner(response);
        String responseBody = scanner.useDelimiter("\\A").next();
        JSONObject jsonWeatherListObject = new JSONObject(responseBody);
        JSONArray weatherList = jsonWeatherListObject.getJSONArray("list");
        List<Weather> weatherObjectsList = parser.convertToListOfWeather(weatherList);

        weatherRepo.deleteAll();
        weatherRepo.save(weatherObjectsList);
    }
}