package backend.parsers.weatherHTML;

import lombok.Data;
import org.jsoup.nodes.Element;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class PrecipitationM implements Measurement {
    private final String URL = "SI=kph&CEL=C&WMO=12650&TIME=all&LEVEL=140&REGION=0001&ART=niederschlag";
    private final String filter = "tr:contains(m2)";

    private Date time;
    private Float amount;   //l/m2
    private Short interval; //hours
    private String type;

    @Override
    public Measurement fromElement(Element el) throws ParseException {
        PrecipitationM p = new PrecipitationM();
        String time = el.child(0).text().replace("\u00a0", " ");
        p.setTime(SIMPLE_DATE_FORMAT.parse(time));

        Pattern pattern = Pattern.compile("\\d+\\.*\\d*");
        String s[] = el.child(1).text().split("l/m2");
        Matcher m = pattern.matcher(s[0]);
        p.setAmount(m.find() ? Util.toFloat(m.group()) : null);

        m = pattern.matcher(s[1]);
        p.setInterval(m.find() ? Util.toShort(m.group()) : null);

        String type = el.child(2).text();
        p.setType(type.equals("-") ? null : type);
        return p;
    }
}
