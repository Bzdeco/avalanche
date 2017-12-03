package weathercollector.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import weathercollector.measurements.WeatherMeasurement;
import weathercollector.measurements.WindMeasurement;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

@Entity
@NoArgsConstructor
@Data
public class Weather
{
    @Id
    private Date time;
    private Float temp;
    private String tempDesc;

    private Short windAvg;     //km/h
    private Short windMax;
    private Short windDirDeg;
    private WindMeasurement.DIRS windDir;

    private Float precipAmount;   //l/m2
    private Short precipInterval; //hours
    private String precipType;

    private Short cloudLevel;   //m
    private Short cloudSum;    //x/8 scale of all clouds
    private Short cloudLow;    //x/8 scale of low clouds

    private Short snowLevel;   //cm

    public Weather(final WeatherMeasurement weatherMeasurement)
    {
        checkNotNull(weatherMeasurement);
        this.time = weatherMeasurement.getTemperatureMeasurement().getTime();
        this.temp = weatherMeasurement.getTemperatureMeasurement().getTemp();
        this.tempDesc = weatherMeasurement.getTemperatureMeasurement().getDesc();
        this.windAvg = weatherMeasurement.getWindMeasurement().getAvgSpeed();
        this.windMax = weatherMeasurement.getWindMeasurement().getMaxSpeed();
        this.windDirDeg = weatherMeasurement.getWindMeasurement().getDirDegree();
        this.windDir = weatherMeasurement.getWindMeasurement().getDir();
        this.precipAmount = weatherMeasurement.getPrecipitationMeasurement().getAmount();
        this.precipInterval = weatherMeasurement.getPrecipitationMeasurement().getInterval();
        this.precipType = weatherMeasurement.getPrecipitationMeasurement().getType();
        this.cloudLevel = weatherMeasurement.getCloudMeasurement().getLevel();
        this.cloudSum = weatherMeasurement.getCloudMeasurement().getSum();
        this.cloudLow = weatherMeasurement.getCloudMeasurement().getLow();
        this.snowLevel = weatherMeasurement.getSnowMeasurement().getLevel();
    }

    public Date getTime()
    {
        return time;
    }
}
