package weatherCollector.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import weatherCollector.parser.dto.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@NoArgsConstructor
@Data
public class Weather {
    @Id
    private Date time;
    private Float temp;
    private String tempDesc;

    private Short windAvg;     //km/h
    private Short windMax;
    private Short windDirDeg;
    private WindM.DIRS windDir;

    private Float precipAmount;   //l/m2
    private Short precipInterval; //hours
    private String precipType;

    private Short cloudLevel;   //m
    private Short cloudSum;    //x/8 scale of all clouds
    private Short cloudLow;    //x/8 scale of low clouds

    private Short snowLevel;   //cm

    public Weather(TempM t, WindM w, PrecipitationM p, CloudsM c, SnowM s) throws IllegalStateException {
        if (t.getTime().compareTo(w.getTime()) != 0 || t.getTime().compareTo(p.getTime()) != 0)
            throw new IllegalStateException("Time differs between measurements.");

        this.time = t.getTime();
        this.temp = t.getTemp();
        this.tempDesc = t.getDesc();
        this.windAvg = w.getAvgSpeed();
        this.windMax = w.getMaxSpeed();
        this.windDirDeg = w.getDirDegree();
        this.windDir = w.getDir();
        this.precipAmount = p.getAmount();
        this.precipInterval = p.getInterval();
        this.precipType = p.getType();
        this.cloudLevel = c.getLevel();
        this.cloudSum = c.getSum();
        this.cloudLow = c.getLow();
        this.snowLevel = s.getLevel();
    }

    public Date getTime()
    {
        return time;
    }
}
