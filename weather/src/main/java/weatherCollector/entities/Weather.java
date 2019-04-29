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
    private Float tempMin;
    private Float tempMax;

    private Float pressure;
    private Float seaLevel;
    private Float grndLevel;

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
        this.tempMin = main.get("temp_min");
        this.tempMax = main.get("temp_max");
        this.pressure = main.get("pressure");
        this.seaLevel = main.get("sea_level");
        this.grndLevel = main.get("grnd_level");
        this.humidity = main.get("humidity");
        this.cloudiness = clouds.get("all");
        this.windSpeed = wind.get("speed");
        this.windDeg = wind.get("deg");
        this.rain = rain == null || rain.get("3h") == null ? new Float(0) : rain.get("3h");
        this.snow = snow == null || snow.get("3h") == null ? new Float(0) : snow.get("3h");
    }

    @Override
    public boolean equals(final Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Weather other = (Weather) o;

        return  Objects.equals(time, other.time) &&
                Objects.equals(temp, other.temp) &&
                Objects.equals(tempMin, other.tempMin) &&
                Objects.equals(tempMax, other.tempMax) &&
                Objects.equals(pressure, other.pressure) &&
                Objects.equals(seaLevel, other.seaLevel) &&
                Objects.equals(grndLevel, other.grndLevel) &&
                Objects.equals(humidity, other.humidity) &&
                Objects.equals(cloudiness, other.cloudiness) &&
                Objects.equals(windSpeed, other.windSpeed) &&
                Objects.equals(windDeg, other.windDeg) &&
                Objects.equals(rain, other.rain) &&
                Objects.equals(snow, other.snow);
    }
}
