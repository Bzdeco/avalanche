package weathercollector.measurements;

import lombok.Data;
import org.jsoup.nodes.Element;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class SnowMeasurement implements Measurement {

    private Date time;
    private Short level;   //cm

    @Override
    public Measurement fromElement(Element el) throws ParseException {
        SnowMeasurement snow = new SnowMeasurement();
        String time = el.child(0).text().replace("\u00a0", " ");
        snow.setTime(SIMPLE_DATE_FORMAT.parse(time));

        String s = el.child(1).text();
        Pattern pattern = Pattern.compile("(\\d)+");
        Matcher m = pattern.matcher(s);
        boolean found = m.find();
        snow.setLevel(found ? Short.parseShort(m.group()) : null);

        return snow;
    }

    @Override
    public String getURL()
    {
        return "CEL=C&SI=kph&WMO=12650&TIME=std&CONT=plpl&R=0&LEVEL=140&LAND=__&ART=snow";
    }

    @Override
    public String getFilter()
    {
        return "tr:matches((\\d\\d.){2}\\d{4})";
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

    public Short getLevel()
    {
        return level;
    }

    public void setLevel(Short level)
    {
        this.level = level;
    }
}