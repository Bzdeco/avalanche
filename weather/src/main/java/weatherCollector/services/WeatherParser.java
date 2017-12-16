package weatherCollector.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.json.JSONArray;
import org.springframework.stereotype.Component;
import weatherCollector.entities.Weather;
import java.io.IOException;
import java.util.List;

@Component
public class WeatherParser {

    public List<Weather> convertToListOfWeather(final JSONArray jsonArray) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        String jsonBody = jsonArray.toString();
        List<Weather> weatherList = objectMapper
                .readValue(jsonBody, typeFactory.constructCollectionType(List.class, Weather.class));

        return weatherList;
    }
}
