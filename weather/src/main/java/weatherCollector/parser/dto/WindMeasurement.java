package weatherCollector.parser.dto;

import lombok.Data;
import org.jsoup.nodes.Element;
import weatherCollector.util.Util;

import java.text.ParseException;
import java.util.Date;

@Data
public class WindMeasurement implements Measurement {
    private final String URL = "ART=wind&SI=kph&WMO=12650&LEVEL=140";
    private final String filter = "tr:contains(°)";
    private Date time;
    private Short avgSpeed;     //km/h
    private Short maxSpeed;
    private Short dirDegree;
    private DIRS dir;

    @Override
    public WindMeasurement fromElement(Element el) throws ParseException {
        WindMeasurement windMeasurement = new WindMeasurement();
        String time = el.child(0).text().replace("\u00a0", " ");
        windMeasurement.setTime(SIMPLE_DATE_FORMAT.parse(time));

        windMeasurement.setAvgSpeed(Util.toShort(el.child(1).text()));
        windMeasurement.setMaxSpeed(Util.toShort(el.child(3).text()));
        windMeasurement.setDirDegree(Util.toShort(el.child(4).text().replace("°", "")));
        windMeasurement.setDir(DIRS.fromString(el.child(5).text().trim()));
        return windMeasurement;
    }

    public enum DIRS {
        NW("płn. zach."),
        N("płn."),
        NE("płn. wsch."),
        E("wsch."),
        SE("płd. wsch."),
        S("płd."),
        SW("płd. zach."),
        W("zach.");

        private String text;

        DIRS(String text) {
            this.text = text;
        }

        public static DIRS fromString(String text) {
            if (text != null)
                for (DIRS d : DIRS.values())
                    if (text.equalsIgnoreCase(d.text))
                        return d;
            throw new IllegalArgumentException("No constant with text " + text + " found.");
        }
    }

    @Override
    public String getURL()
    {
        return URL;
    }

    @Override
    public String getFilter()
    {
        return filter;
    }

    @Override
    public Date getTime()
    {
        return time;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }

    public Short getAvgSpeed()
    {
        return avgSpeed;
    }

    public void setAvgSpeed(Short avgSpeed)
    {
        this.avgSpeed = avgSpeed;
    }

    public Short getMaxSpeed()
    {
        return maxSpeed;
    }

    public void setMaxSpeed(Short maxSpeed)
    {
        this.maxSpeed = maxSpeed;
    }

    public Short getDirDegree()
    {
        return dirDegree;
    }

    public void setDirDegree(Short dirDegree)
    {
        this.dirDegree = dirDegree;
    }

    public DIRS getDir()
    {
        return dir;
    }

    public void setDir(DIRS dir)
    {
        this.dir = dir;
    }
}
