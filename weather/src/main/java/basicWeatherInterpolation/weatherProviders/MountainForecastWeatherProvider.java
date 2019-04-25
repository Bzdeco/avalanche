package basicWeatherInterpolation.weatherProviders;

import lombok.Builder;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import weatherCollector.entities.Weather;

import javax.validation.constraints.Null;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MountainForecastWeatherProvider implements WeatherProvider {
    @Data
    @Builder
    private static class Peak
    {
        private String name;
        private String url;
        private int height;
        private float latitude;
        private float longitude;
        @Null
        private Weather weather;
    }

    private String baseUrl = "https://www.mountain-forecast.com";
    private String peaksListUrl = "/subranges/tatras/locations";
    public List<Peak> peaks;


    private Map<String,Float> textDirectionToDegree = Stream.of(
            new Object[][]{
                    {"N",0.f},{"NNE",22.5f},{"NE",45.f},
                    {"ENE",67.5f},{"E",90.f},{"ESE",112.f},
                    {"SE",135.f},{"SSE",157.5f},{"S",180.f},
                    {"SSW",202.5f},
                    {"SW",225.f},
                    {"WSW",247.5f},
                    {"W",270.f},
                    {"WNW",292.5f},
                    {"NW",315.f},
                    {"NNW",337.5f},
            }).collect(Collectors.toMap(data ->(String)data[0], data->(Float)data[1]));

    public MountainForecastWeatherProvider(){
        try{
            Document document = Jsoup.connect(baseUrl + peaksListUrl).get();
            readPeaks(document);
            readDataFromPeaks();
        }catch(Exception e){

        }

    }

    private void readPeaks(Document document){
        Elements rawPeaks = document.body().select(".b-list-table").select("li");
        peaks = rawPeaks.stream()
                .map(MountainForecastWeatherProvider::mapToPeak)
                .collect(Collectors.toList());
    }

    private static Peak mapToPeak(Element element)
    {
        String name = element.select("span.b-list-table__item-name a").text();
        String url = element.select("span.b-list-table__item-name a").attr("href");
        int height = Integer.parseInt(element.select("span.b-list-table__item-height").text()
                .replaceAll("\\D+",""));

        return Peak.builder()
                .name(name)
                .url(url)
                .height(height)
                .build();
    }

    private void readDataFromPeaks() throws Exception
    {
        for (Peak peak : peaks) {
            parsePeak(peak);
        }
    }
    private void parsePeak(Peak peak) throws IOException {
        String url = baseUrl+peak.getUrl();
        Document document = Jsoup.connect(url).get();
        Elements table = document.select(".forecast__table");
        float windSpeed = getWindSpeed(table);
        float windDeg = getWindDirection(table);
        float rain = getRain(table);
        float snow = getSnow(table);
        float maxTemp = getMaxTemp(table);
        float minTemp = getMinTemp(table);

        Weather weather = new Weather();
        weather.setWind_speed(windSpeed);
        weather.setWind_deg(windDeg);
        weather.setWind_speed(windSpeed);
        weather.setRain(rain);
        weather.setSnow(snow);
        weather.setTemp_max(maxTemp);
        weather.setTemp_min(minTemp);
        weather.setSea_level((float)peak.height);
        peak.weather = weather;

        //retrive lon, lat
        Optional<Element> script = document.select("script[type='text/javascript']").stream()
                .filter(x -> x.toString().contains("FCOSM.initMapForLocation"))
                .findFirst();
        if(!script.isPresent())
            throw new RuntimeException("The script with lon and lat was not found.");
        String content = script.get().toString();
        int start = content.indexOf('(');
        int end = content.indexOf(')');
        String[] split = content.substring(start + 1, end).split(",");
        float lat = Float.parseFloat(split[1]);
        float lng = Float.parseFloat(split[2]);
        peak.latitude = lat;
        peak.longitude = lng;
    }
    private float getMinTemp(Elements table) {
        Elements minTempRow = table.select(".forecast__table-min-temperature");
        String value = minTempRow.select("td span.temp").get(0).text();
        return Float.parseFloat(value);
    }

    private float getMaxTemp(Elements table) {
        Elements maxTempRow = table.select(".forecast__table-max-temperature");
        String value = maxTempRow.select("td span.temp").get(0).text();
        return Float.parseFloat(value);
    }

    private float getSnow(Elements table) {
        Elements snowRow = table.select(".forecast__table-snow");
        String value = snowRow.select("td span.snow").get(0).text().replaceAll("\\D+","0");
        return Float.parseFloat(value);
    }

    private float getRain(Elements table) {
        Elements rainRow = table.select(".forecast__table-rain");
        String value = rainRow.select("td span.rain").get(0).text().replaceAll("\\D+","0");
        return Float.parseFloat(value);
    }

    private float getWindSpeed(Elements table)
    {
        Elements wind = table.select(".forecast__table-wind");
        Elements speeds = wind.select("td.iconcell span");
        String text = speeds.get(0).text();
        return Float.parseFloat(text);
    }
    private float getWindDirection(Elements table)
    {
        Elements wind = table.select(".forecast__table-wind");
        String alt = wind.select("td.iconcell img").attr("alt").replaceAll("[^A-Z]+","");
        return this.textDirectionToDegree.get(alt.toUpperCase());
    }

    @Override
    public float getLatitude() {
        return 0;
    }

    @Override
    public float getLongitude() {
        return 0;
    }

    @Override
    public float getElevation() {
        return 0;
    }

    @Override
    public Weather currentWeather() {
        Weather weather = new Weather();
        return weather;
    }
}
