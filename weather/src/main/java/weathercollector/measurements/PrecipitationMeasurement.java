package weathercollector.measurements;

import lombok.Data;
import org.jsoup.nodes.Element;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class PrecipitationMeasurement implements Measurement {

    private Date time;
    private Float amount;   //l/m2
    private Short interval; //hours
    private String type;

    @Override
    public Measurement fromElement(Element el) throws ParseException {
        PrecipitationMeasurement p = new PrecipitationMeasurement();
        String time = el.child(0).text().replace("\u00a0", " ");
        p.setTime(SIMPLE_DATE_FORMAT.parse(time));

        Pattern pattern = Pattern.compile("\\d+\\.*\\d*");
        String s[] = el.child(1).text().split("l/m2");
        Matcher m = pattern.matcher(s[0]);
        boolean found = m.find();
        p.setAmount(found ? Float.parseFloat(m.group()) : null);
        if (found) {
            m = pattern.matcher(s[1]);
        }
        p.setInterval(m.find() ? Short.parseShort(m.group()) : null);

        String type = el.child(2).text();
        p.setType(type.equals("-") ? null : type);
        return p;
    }

    @Override
    public String getURL()
    {
        return "CEL=C&SI=kph&WMO=12650&TIME=std&LEVEL=140&ART=niederschlag";
    }

    @Override
    public String getFilter()
    {
        return "tr:contains(m2), tr:contains(brak komunikatu)";
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

    public Float getAmount()
    {
        return amount;
    }

    public void setAmount(Float amount)
    {
        this.amount = amount;
    }

    public Short getInterval()
    {
        return interval;
    }

    public void setInterval(Short interval)
    {
        this.interval = interval;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
