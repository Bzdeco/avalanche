package weatherCollector.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import weatherCollector.parser.dto.PrecipitationM;
import weatherCollector.parser.dto.TempM;
import weatherCollector.parser.dto.WindM;

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

    public Weather(TempM t, WindM w, PrecipitationM p) throws IllegalStateException {
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
    }
}
