package org.avalanche.weather.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Entity
@Table(name = "weather")
@Data
@JsonDeserialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class Weather {
    @Id
    private LocalDateTime time;

    private Float temp;
    private Float tempMin;
    private Float tempMax;

    private Float pressure;
    private Float seaLevel;
    private Float grndLevel;

    private Float humidity;

    private Float cloudiness;

    private Float windSpeed;
    private Float windDeg;

    private Float rain;
    private Float snow;

    public Weather() {
    }

    @JsonCreator
    public Weather(@JsonProperty("dt") Long time, @JsonProperty("main") Map<String, Float> main,
                   @JsonProperty("clouds") Map<String, Float> clouds, @JsonProperty("wind") Map<String, Float> wind,
                   @JsonProperty("rain") Map<String, Float> rain, @JsonProperty("snow") Map<String, Float> snow) {
        this.time = LocalDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.of("Europe/Warsaw"));
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
}
