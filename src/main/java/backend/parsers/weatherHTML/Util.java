package backend.parsers.weatherHTML;

public class Util {
    public static Integer toInt(String s){
        try{
            return Integer.parseInt(s);
        }
        catch (NumberFormatException e){
            return null;
        }
    }

    public static Float toFloat(String s){
        try{
            return Float.parseFloat(s);
        }
        catch (NumberFormatException e){
            return null;
        }
    }

    public static Short toShort(String s){
        try{
            return Short.parseShort(s);
        }
        catch (NumberFormatException e){
            return null;
        }
    }

}
