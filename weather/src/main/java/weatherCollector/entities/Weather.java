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

    public Weather(){}

    @JsonCreator
    public Weather(@JsonProperty("dt") Long time, @JsonProperty("main") Map<String, Float> main,
                   @JsonProperty("clouds") Map<String, Float> clouds, @JsonProperty("wind") Map<String, Float> wind,
                   @JsonProperty("rain") Map<String, Float> rain, @JsonProperty("snow") Map<String, Float> snow) {
        this.time = new Date(time*1000L);
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
}
