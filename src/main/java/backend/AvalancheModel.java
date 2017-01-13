package backend;

import dto.WeatherDto;
import javafx.collections.ObservableList;

public class AvalancheModel {
    private WeatherDto currentWeather;

    synchronized public WeatherDto getCurrentWeather() {
        return currentWeather;
    }

    synchronized public void setCurrentWeather(ObservableList<String> row) {
        this.currentWeather = new WeatherDto(row);
    }
}
