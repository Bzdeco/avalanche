package weatherCollector.parser.dto;

import lombok.Data;
import org.jsoup.nodes.Element;
import weatherCollector.util.Util;

import java.text.ParseException;
import java.util.Date;

@Data
public class TempM implements Measurement {
    private final String URL = "WMO=12650&LEVEL=140";
    private final String filter = "tr:contains(°C)";
    private Date time;
    private Float temp;
    private String desc;

    @Override
    public TempM fromElement(Element el) throws ParseException {
        TempM tempM = new TempM();
        String temperature = el.child(1).text().replace("°C", "");
        tempM.setTemp(Util.toFloat(temperature));

        String time = el.child(0).text().replace("\u00a0", " ");
        tempM.setTime(SIMPLE_DATE_FORMAT.parse(time));

        tempM.setDesc(el.child(2).text());
        return tempM;
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
