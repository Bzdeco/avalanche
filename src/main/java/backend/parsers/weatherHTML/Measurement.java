package backend.parsers.weatherHTML;

import org.jsoup.nodes.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public interface Measurement {
    SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    Measurement fromElement(Element element) throws ParseException;

    String getFilter();

    String getURL();
}
