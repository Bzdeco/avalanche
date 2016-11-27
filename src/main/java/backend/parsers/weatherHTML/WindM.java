package backend.parsers.weatherHTML;

import lombok.Data;
import org.jsoup.nodes.Element;

import java.text.ParseException;
import java.util.Date;

@Data
public class WindM implements Measurement {
    private final String URL = "ART=wind&SI=kph&WMO=12650&LEVEL=140";
    private final String filter = "tr:contains(°)";
    private Date time;
    private Short avgSpeed;     //km/h
    private Short maxSpeed;
    private Short dirDegree;
    private DIRS dir;

    @Override
    public WindM fromElement(Element el) throws ParseException {
        WindM windM = new WindM();
        String time = el.child(0).text().replace("\u00a0", " ");
        windM.setTime(SIMPLE_DATE_FORMAT.parse(time));

        windM.setAvgSpeed(Util.toShort(el.child(1).text()));
        windM.setMaxSpeed(Util.toShort(el.child(3).text()));
        windM.setDirDegree(Util.toShort(el.child(4).text().replace("°", "")));
        windM.setDir(DIRS.fromString(el.child(5).text().trim()));
        return windM;
    }

    private enum DIRS {
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
}
