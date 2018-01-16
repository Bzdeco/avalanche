package avalanche.model.database;

import javafx.collections.ObservableList;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Class representing single weather measurement
 */
@Data
public class WeatherDto {

    private Date time;

    private Float temp;
    private Float temp_min;
    private Float temp_max;

    private Float pressure;
    private Float sea_level;
    private Float grnd_level;

    private Float humidity;

    private Float cloudiness;

    private Float wind_speed;
    private Float wind_deg;

    private Float rain;
    private Float snow;

    public WeatherDto(Builder builder) {
        this.time = builder.time;
        this.temp = builder.temp;
        this.temp_min = builder.temp_min;
        this.temp_max = builder.temp_max;
        this.pressure = builder.pressure;
        this.sea_level = builder.sea_level;
        this.grnd_level = builder.grnd_level;
        this.humidity = builder.humidity;
        this.cloudiness = builder.cloudiness;
        this.wind_speed = builder.wind_speed;
        this.wind_deg = builder.wind_deg;
        this.rain = builder.rain;
        this.snow = builder.snow;
    }

    public Date getTime() {
        return time;
    }

    public Float getTemp() {
        return temp;
    }

    public Float getTemp_min() {
        return temp_min;
    }

    public Float getTemp_max() {
        return temp_max;
    }

    public Float getPressure() {
        return pressure;
    }

    public Float getSea_level() {
        return sea_level;
    }

    public Float getGrnd_level() {
        return grnd_level;
    }

    public Float getHumidity() {
        return humidity;
    }

    public Float getCloudiness() {
        return cloudiness;
    }

    public Float getWind_speed() {
        return wind_speed;
    }

    public Float getWind_deg() {
        return wind_deg;
    }

    public Float getRain() {
        return rain;
    }

    public Float getSnow() {
        return snow;
    }

    public static class Builder {

        private Date time;

        private Float temp;
        private Float temp_min;
        private Float temp_max;

        private Float pressure;
        private Float sea_level;
        private Float grnd_level;

        private Float humidity;

        private Float cloudiness;

        private Float wind_speed;
        private Float wind_deg;

        private Float rain;
        private Float snow;


        public Builder() {}

        public Builder time(String val) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime d = formatter.parse(val, LocalDateTime::from);
            this.time = Date.from(d.atZone(ZoneId.systemDefault()).toInstant());
            return this;
        }

        public Builder temp(String val) {
            temp = Float.parseFloat(val);
            return this;
        }

        public Builder temp_min(String val) {
            temp_min = Float.parseFloat(val);
            return this;
        }

        public Builder temp_max(String val) {
            temp_max = Float.parseFloat(val);
            return this;
        }

        public Builder pressure(String val) {
            pressure = Float.parseFloat(val);
            return this;
        }

        public Builder grnd_level(String val) {
            grnd_level = Float.parseFloat(val);
            return this;
        }

        public Builder sea_level(String val) {
            sea_level = Float.parseFloat(val);
            return this;
        }

        public Builder humidity(String val) {
            humidity = Float.parseFloat(val);
            return this;
        }

        public Builder cloudiness(String val) {
            cloudiness = Float.parseFloat(val);
            return this;
        }

        public Builder wind_speed(String val) {
            wind_speed = Float.parseFloat(val);
            return this;
        }

        public Builder wind_deg(String val) {
            wind_deg = Float.parseFloat(val);
            return this;
        }

        public Builder rain(String val) {
            rain = Float.parseFloat(val);
            return this;
        }

        public Builder snow(String val) {
            snow = Float.parseFloat(val);
            return this;
        }

        public WeatherDto build(ObservableList<String> row) {
            time(row.get(0)).temp(row.get(1)).temp_min(row.get(2)).temp_max(row.get(3)).pressure(row.get(4))
                    .sea_level(row.get(5)).grnd_level(row.get(6)).humidity(row.get(7)).cloudiness(row.get(8))
                    .wind_speed(row.get(9)).wind_deg(row.get(10)).rain(row.get(11)).snow(row.get(12));
            return new WeatherDto(this);
        }
    }
}
