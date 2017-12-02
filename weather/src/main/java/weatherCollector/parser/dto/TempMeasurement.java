package weatherCollector.parser.dto;

import lombok.Data;
import org.jsoup.nodes.Element;
import weatherCollector.util.Util;

import java.text.ParseException;
import java.util.Date;

@Data
public class TempMeasurement implements Measurement {
    private final String URL = "WMO=12650&LEVEL=140";
    private final String filter = "tr:contains(°C)";
    private Date time;
    private Float temp;
    private String desc;

    @Override
    public TempMeasurement fromElement(Element el) throws ParseException {
        TempMeasurement tempMeasurement = new TempMeasurement();
        String temperature = el.child(1).text().replace("°C", "");
        tempMeasurement.setTemp(Util.toFloat(temperature));

        String time = el.child(0).text().replace("\u00a0", " ");
        tempMeasurement.setTime(SIMPLE_DATE_FORMAT.parse(time));

        tempMeasurement.setDesc(el.child(2).text());
        return tempMeasurement;
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

    public Float getTemp()
    {
        return temp;
    }

    public void setTemp(Float temp)
    {
        this.temp = temp;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }
}
