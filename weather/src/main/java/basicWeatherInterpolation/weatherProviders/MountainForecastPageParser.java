package basicWeatherInterpolation.weatherProviders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import weatherCollector.entities.Weather;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MountainForecastPageParser {

    private static final String BASE_URL = "https://www.mountain-forecast.com";
    private static final String PEAKS_LIST_URL = "/subranges/tatras/locations";
    private List<Peak> peaks;

    private Map<String,Float> textDirectionToDegree = Stream.of(new Object[][]{
            {"N",0.f},
            {"NNE",22.5f},
            {"NE",45.f},
            {"ENE",67.5f},
            {"E",90.f},
            {"ESE",112.f},
            {"SE",135.f},
            {"SSE",157.5f},
            {"S",180.f},
            {"SSW",202.5f},
            {"SW",225.f},
            {"WSW",247.5f},
            {"W",270.f},
            {"WNW",292.5f},
            {"NW",315.f},
            {"NNW",337.5f},
    }).collect(Collectors.toMap(data ->(String)data[0], data->(Float)data[1]));

    public MountainForecastPageParser(){
        try{
            Document document = Jsoup.connect(BASE_URL + PEAKS_LIST_URL).get();
            readPeaks(document);
            readDataFromPeaks();
        }catch(Exception e){
            System.out.println("Some error occuried while parsing data.");
            throw new RuntimeException();
        }
    }

    Peak getPeak(PeakName name){
        return this.peaks.stream()
                .filter(x -> x.getName().equals(name.name()))
                .findFirst()
                .orElseThrow(()->new RuntimeException("PeakName was not found"));
    }

    private void readPeaks(Document document){
        Elements rawPeaks = document.body().select(".b-list-table").select("li");
        peaks = rawPeaks.stream()
                .map(MountainForecastPageParser::mapToPeak)
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
        String url = BASE_URL +peak.getUrl()+"?skip_layout=true&mode=detailed";
        Document document = Jsoup.connect(url).get();

        Elements table = document.select(".forecast__table");
        int columnIndex = findMatchingColumnByDateTime(table);

        Date dateTime = getDateTime(table,columnIndex);
        float windSpeed = getWindSpeed(table, columnIndex);
        float windDeg = getWindDirection(table, columnIndex);
        float rain = getRain(table,columnIndex);
        float snow = getSnow(table,columnIndex);
        float maxTemp = getMaxTemp(table,columnIndex);
        Float minTemp = getMinTemp(table,columnIndex);
        if(minTemp == null)
            minTemp = maxTemp;

        Weather weather = new Weather();
        weather.setTime(dateTime);
        weather.setWindSpeed(windSpeed);
        weather.setWindDeg(windDeg);
        weather.setWindSpeed(windSpeed);
        weather.setRain(rain);
        weather.setSnow(snow);
        weather.setTempMax(maxTemp);
        weather.setTempMin(minTemp);
        weather.setTemp((minTemp + maxTemp) / 2);
        weather.setSeaLevel((float)peak.getHeight());
        peak.setWeather(weather);

        url = BASE_URL +peak.getUrl();
        document = Jsoup.connect(url).get();

        retrieveCoordinates(document,peak);
    }

    private int findMatchingColumnByDateTime(Elements table) {
        LocalDateTime currentDateTime = LocalDateTime.now();

        List<Integer> days = table.select("td.forecast__table-days-item")
                .stream()
                .map(x -> Integer.parseInt(x.attr("data-value")))
                .collect(Collectors.toList());
        int currentDay = currentDateTime.getDayOfMonth();
        int currentDayIndex = days.indexOf(currentDay);

        Elements rawTimes = table.select("td.forecast__table-time-item");
        List<Integer> dayEndMarkers = new ArrayList<>();
        for (int i = 0; i < rawTimes.size(); i++) {
            Element item = rawTimes.get(i);
            if(item.hasClass("forecast__table-day-end")){
                dayEndMarkers.add(i);
            }
        }

        int startIndex = 0;
        int endIndex = dayEndMarkers.get(currentDayIndex);
        if(currentDayIndex != 0)
            startIndex = dayEndMarkers.get(currentDayIndex-1)+1;
        List<Element> elements = rawTimes.subList(startIndex, endIndex+1);

        int minIdx = IntStream.range(0,elements.size()).boxed()
                .min(Comparator.comparingLong(i -> Math.abs(getTimeFrom(elements.get(i)).until(currentDateTime, ChronoUnit.MINUTES))))
                .get();
        return startIndex + minIdx;
    }
    private LocalTime getTimeFrom(Element element)
    {
        int thinSpaceCode = 8201;
        String time = element.text().replaceAll(Character.toString((char)thinSpaceCode)," ");
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("h a", Locale.ENGLISH));
    }
    private Date getDateTime(Elements table,int columnIndex){
        LocalTime time = getTimeFrom(table.select("td.forecast__table-time-item").get(columnIndex));
        return java.sql.Timestamp.valueOf(time.atDate(LocalDate.now()));
    }

    private Float getMinTemp(Elements table, int columnIndex) {
        Elements minTempRow = table.select(".forecast__table-min-temperature");
        if(minTempRow.isEmpty())
            return null;
        String value = minTempRow.select("td span.temp").get(columnIndex).text();
        return Float.parseFloat(value);
    }

    private float getMaxTemp(Elements table, int columnIndex) {
        Elements maxTempRow = table.select(".forecast__table-max-temperature");
        String value = maxTempRow.select("td span.temp").get(columnIndex).text();
        return Float.parseFloat(value);
    }

    private float getSnow(Elements table, int columnIndex) {
        Elements snowRow = table.select(".forecast__table-snow");
        String value = snowRow.select("td span.snow").get(columnIndex).text().replaceAll("\\D+","0");
        return Float.parseFloat(value);
    }

    private float getRain(Elements table, int columnIndex) {
        Elements rainRow = table.select(".forecast__table-rain");
        String value = rainRow.select("td span.rain").get(columnIndex).text().replaceAll("\\D+","0");
        return Float.parseFloat(value);
    }

    private float getWindSpeed(Elements table, int columnIndex)
    {
        Elements wind = table.select(".forecast__table-wind");
        Elements speeds = wind.select("td.iconcell span");
        String text = speeds.get(columnIndex).text();
        return Float.parseFloat(text);
    }

    private float getWindDirection(Elements table, int columnIndex)
    {
        Elements wind = table.select(".forecast__table-wind");
        String alt = wind.select("td.iconcell img").get(columnIndex).attr("alt").replaceAll("[^A-Z]+","");
        return this.textDirectionToDegree.get(alt.toUpperCase());
    }

    private void retrieveCoordinates(Document document, Peak peak)
    {
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
        peak.setLatitude(lat);
        peak.setLongitude(lng);
    }

}
