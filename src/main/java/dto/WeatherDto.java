package dto;

import backend.Utils.Dirs;
import backend.Utils.Util;
import javafx.collections.ObservableList;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
public class WeatherDto {
    private Date time;

    public Date getTime() {
        return time;
    }

    public Float getTemp() {
        return temp;
    }

    public String getTempDesc() {
        return tempDesc;
    }

    public Short getWindAvg() {
        return windAvg;
    }

    public Short getWindMax() {
        return windMax;
    }

    public Short getWindDirDeg() {
        return windDirDeg;
    }

    public Dirs getWindDir() {
        return windDir;
    }

    public Float getPrecipAmount() {
        return precipAmount;
    }

    public Short getPrecipInterval() {
        return precipInterval;
    }

    public String getPrecipType() {
        return precipType;
    }

    public Short getCloudLevel() {
        return cloudLevel;
    }

    public Short getCloudSum() {
        return cloudSum;
    }

    public Short getCloudLow() {
        return cloudLow;
    }

    public Short getSnowLevel() {
        return snowLevel;
    }

    private Float temp;
    private String tempDesc;

    private Short windAvg;     //km/h
    private Short windMax;
    private Short windDirDeg;
    private Dirs windDir;

    private Float precipAmount;   //l/m2
    private Short precipInterval; //hours
    private String precipType;

    private Short cloudLevel;   //m
    private Short cloudSum;    //x/8 scale of all clouds
    private Short cloudLow;    //x/8 scale of low clouds

    private Short snowLevel;   //cm

    public WeatherDto(ObservableList<String> row) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime d = formatter.parse(row.get(0), LocalDateTime::from);
        this.time = Date.from(d.atZone(ZoneId.systemDefault()).toInstant());

        this.temp = Util.toFloat(row.get(1));
        this.tempDesc = row.get(2);
        this.windAvg = Util.toShort(row.get(3));
        this.windMax = Util.toShort(row.get(4));
        this.windDirDeg = Util.toShort(row.get(5));
        Short windDirEnum = Util.toShort(row.get(6));
        this.windDir = windDirEnum == null? null : Dirs.values()[windDirEnum];
        this.precipAmount = Util.toFloat(row.get(7));
        this.precipInterval = Util.toShort(row.get(8));
        this.precipType = row.get(9);
        this.cloudLevel = Util.toShort(row.get(10));
        this.cloudSum = Util.toShort(row.get(11));
        this.cloudLow = Util.toShort(row.get(12));
        this.snowLevel = Util.toShort(row.get(13));
    }
}
