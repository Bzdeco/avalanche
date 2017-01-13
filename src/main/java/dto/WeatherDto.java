package dto;

import backend.Utils.Dirs;
import backend.Utils.Util;
import javafx.collections.ObservableList;
import lombok.Data;

import java.sql.Date;

@Data
public class WeatherDto {
    private Date time;
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
//        this.time = (row.get(0) == null? null : Date.valueOf(row.get(0).toString()));
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
