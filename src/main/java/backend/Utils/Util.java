package backend.Utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
    private static final Logger logger = LogManager.getLogger();

    public static Integer toInt(String s){
        try{
            return Integer.parseInt(s);
        }
        catch (NumberFormatException | NullPointerException e){
            logger.debug(e);
            return null;
        }
    }

    public static Float toFloat(String s){
        try{
            return Float.parseFloat(s);
        }
        catch (NumberFormatException | NullPointerException e){
            logger.debug(e);
            return null;
        }
    }

    public static Short toShort(String s){
        try{
            return Short.parseShort(s);
        }
        catch (NumberFormatException | NullPointerException e){
            logger.debug(e);
            return null;
        }
    }

}
