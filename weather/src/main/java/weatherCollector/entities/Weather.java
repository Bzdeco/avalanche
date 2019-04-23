package weatherCollector.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Entity
@Data
@JsonDeserialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class Weather {
    @Id
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

    public Weather() {
    }

    @JsonCreator
    public Weather(@JsonProperty("dt") Long time, @JsonProperty("main") Map<String, Float> main,
                   @JsonProperty("clouds") Map<String, Float> clouds, @JsonProperty("wind") Map<String, Float> wind,
                   @JsonProperty("rain") Map<String, Float> rain, @JsonProperty("snow") Map<String, Float> snow) {
        this.time = new Date(time * 1000L);
        this.temp = main.get("temp");
        this.temp_min = main.get("temp_min");
        this.temp_max = main.get("temp_max");
        this.pressure = main.get("pressure");
        this.sea_level = main.get("sea_level");
        this.grnd_level = main.get("grnd_level");
        this.humidity = main.get("humidity");
        this.cloudiness = clouds.get("all");
        this.wind_speed = wind.get("speed");
        this.wind_deg = wind.get("deg");
        this.rain = rain == null || rain.get("3h") == null ? new Float(0) : rain.get("3h");
        this.snow = snow == null || snow.get("3h") == null ? new Float(0) : snow.get("3h");
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = new Date(time * 1000L);
    }

    public Float getTemp() {
        return temp;
    }

    public void setTemp(Float temp) {
        this.temp = temp;
    }

    public Float getTempMin() {
        return temp_min;
    }

    public void setTempMin(Float temp_min) {
        this.temp_min = temp_min;
    }

    public Float getTempMax() {
        return temp_max;
    }

    public void setTempMax(Float temp_max) {
        this.temp_max = temp_max;
    }

    public Float getPressure() {
        return pressure;
    }

    public void setPressure(Float pressure) {
        this.pressure = pressure;
    }

    public Float getSeaLevel() {
        return sea_level;
    }

    public void setSeaLevel(Float sea_level) {
        this.sea_level = sea_level;
    }

    public Float getGrndLevel() {
        return grnd_level;
    }

    public void setGrndLevel(Float grnd_level) {
        this.grnd_level = grnd_level;
    }

    public Float getHumidity() {
        return humidity;
    }

    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }

    public Float getCloudiness() {
        return cloudiness;
    }

    public void setCloudiness(Float cloudiness) {
        this.cloudiness = cloudiness;
    }

    public Float getWindSpeed() {
        return wind_speed;
    }

    public void setWindSpeed(Float speed) {
        this.wind_speed = speed;
    }

    public Float getWindDeg() {
        return wind_deg;
    }

    public void setWinDeg(Float deg) {
        this.wind_deg = deg;
    }

    public Float getRain() {
        return rain;
    }

    public void setRain(Float rain) {
        this.rain = rain;
    }

    public Float getSnow() {
        return snow;
    }

    public void setSnow(Float snow) {
        this.snow = snow;
    }


    @Override
    public boolean equals(final Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Weather other = (Weather) o;

        return  Objects.equals(time, other.time) &&
                Objects.equals(temp, other.temp) &&
                Objects.equals(temp_min, other.temp_min) &&
                Objects.equals(temp_max, other.temp_max) &&
                Objects.equals(pressure, other.pressure) &&
                Objects.equals(sea_level, other.sea_level) &&
                Objects.equals(grnd_level, other.grnd_level) &&
                Objects.equals(humidity, other.humidity) &&
                Objects.equals(cloudiness, other.cloudiness) &&
                Objects.equals(wind_speed, other.wind_speed) &&
                Objects.equals(wind_deg, other.wind_deg) &&
                Objects.equals(rain, other.rain) &&
                Objects.equals(snow, other.snow);
    }
}
