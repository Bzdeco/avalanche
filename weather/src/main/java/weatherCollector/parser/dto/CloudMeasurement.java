package weatherCollector.parser.dto;

import lombok.Data;
import org.jsoup.nodes.Element;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class CloudMeasurement implements Measurement {
    private final String URL = "CEL=C&SI=kph&WMO=12650&TIME=std&LEVEL=140&ART=wolken";
    private final String filter = "tr:matches((\\d\\d.){2}\\d{4})";

    private Date time;
    private Short level;   //m
    private Short sum; //sum of high and low Clouds scale x/8
    private Short low; //low Clouds scale x/8

    @Override
    public Measurement fromElement(Element el) throws ParseException {
        CloudMeasurement c = new CloudMeasurement();
        String time = el.child(0).text().replace("\u00a0", " ");
        c.setTime(SIMPLE_DATE_FORMAT.parse(time));
        String s2,s3;
        String s = el.child(1).text();
        Pattern pattern = Pattern.compile("(\\d)+ m");
        Matcher m = pattern.matcher(s);
        boolean found = m.find();

        c.setLevel(found ? Short.parseShort(m.group().replace("m", "").trim()) : null);

        Pattern pattern2 = Pattern.compile("\\d/\\d");

        m.reset();
        m.usePattern(pattern2);
        if(m.find())
            c.setSum(Short.parseShort(m.group().substring(0, 1)));
        if(m.find())
            c.setLow(Short.parseShort(m.group().substring(0, 1)));

        return c;
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

    public Short getLevel()
    {
        return level;
    }

    public void setLevel(Short level)
    {
        this.level = level;
    }

    public Short getSum()
    {
        return sum;
    }

    public void setSum(Short sum)
    {
        this.sum = sum;
    }

    public Short getLow()
    {
        return low;
    }

    public void setLow(Short low)
    {
        this.low = low;
    }
}