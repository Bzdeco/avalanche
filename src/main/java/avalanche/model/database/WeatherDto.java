package avalanche.model.database;

import avalanche.model.Dirs;
import old.Utils.Util;
import javafx.collections.ObservableList;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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

    private WeatherDto(Builder builder) {
        this.time = builder.time;
        this.temp = builder.temp;
        this.tempDesc = builder.tempDesc;
        this.windAvg = builder.windAvg;
        this.windMax = builder.windMax;
        this.windDirDeg = builder.windDirDeg;
        this.windDir = builder.windDir;
        this.precipAmount = builder.precipAmount;
        this.precipInterval = builder.precipInterval;
        this.precipType = builder.precipType;
        this.cloudLevel = builder.cloudLevel;

        this.cloudSum = builder.cloudSum;
        this.cloudLow = builder.cloudLevel;
        this.snowLevel = builder.snowLevel;
    }

    public Date getTime()
    {
        return time;
    }

    public Float getTemp()
    {
        return temp;
    }

    public String getTempDesc()
    {
        return tempDesc;
    }

    public Short getWindAvg()
    {
        return windAvg;
    }

    public Short getWindMax()
    {
        return windMax;
    }

    public Short getWindDirDeg()
    {
        return windDirDeg;
    }

    public Dirs getWindDir()
    {
        return windDir;
    }

    public Float getPrecipAmount()
    {
        return precipAmount;
    }

    public Short getPrecipInterval()
    {
        return precipInterval;
    }

    public String getPrecipType()
    {
        return precipType;
    }

    public Short getCloudLevel()
    {
        return cloudLevel;
    }

    public Short getCloudSum()
    {
        return cloudSum;
    }

    public Short getCloudLow()
    {
        return cloudLow;
    }

    public Short getSnowLevel()
    {
        return snowLevel;
    }

    public static class Builder {
        private Date time;
        private Float temp;

        private String tempDesc = null;

        private Short windAvg = null;     //km/h
        private Short windMax = null;
        private Short windDirDeg = null;
        private Dirs windDir = null;

        private Float precipAmount = null;   //l/m2
        private Short precipInterval = null; //hours
        private String precipType = null;

        private Short cloudLevel = null;   //m
        private Short cloudSum = null;    //x/8 scale of all clouds
        private Short cloudLow = null;    //x/8 scale of low clouds

        private Short snowLevel = null;   //cm

        public Builder() {
        }

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

        public Builder tempDesc(String desc) {
            tempDesc = desc;
            return this;
        }

        public Builder windAvg(String val) {
            windAvg = Short.parseShort(val);
            return this;
        }

        public Builder windMax(String val) {
            windMax = Short.parseShort(val);
            return this;
        }

        public Builder windDirDeg(String val) {

            windDirDeg = Short.parseShort(val);
            return this;
        }

        public Builder windDir(String val) {
            Short windDirEnum = Short.parseShort(val);
            this.windDir = windDirEnum == null ? null : Dirs.values()[windDirEnum];
            return this;
        }

        public Builder precipAmount(String val) {
            precipAmount = Float.parseFloat(val);
            return this;
        }

        public Builder precipInterval(String val) {
            precipInterval = Short.parseShort(val);
            return this;
        }

        public Builder precipType(String val) {
            precipType = val;
            return this;
        }

        public Builder cloudLevel(String val) {
            cloudLevel = Short.parseShort(val);
            return this;
        }

        public Builder cloudSum(String val) {
            cloudSum = Short.parseShort(val);
            return this;
        }

        public Builder cloudLow(String val) {
            cloudLow = Short.parseShort(val);
            return this;
        }

        public Builder snowLevel(String val) {
            snowLevel = Short.parseShort(val);
            return this;
        }


        public WeatherDto build(ObservableList<String> row) {
            time(row.get(0)).temp(row.get(1)).tempDesc(row.get(2)).windAvg(row.get(3)).windMax(row.get(4))
                    .windDirDeg(row.get(5)).windDir(row.get(6)).precipAmount(row.get(7)).precipInterval(row.get(8))
                    .precipType(row.get(9)).cloudLevel(row.get(10)).cloudSum(row.get(11)).cloudLow(row.get(12))
                    .snowLevel(row.get(13));
            return new WeatherDto(this);
        }
    }
}
