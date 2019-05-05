package weatherCollector.coordinates;

import lombok.Data;

@Data
public class Coords
{
    private Float latitude;
    private Float longitude;
    private Float elevation;

    public Coords(float latitude, float longitude){
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = 0f;
    }

    public Coords(float latitude, float longitude, float elevation){
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
    }

    public double distance(Coords other){
        double a = getLatitude()  - other.getLatitude();
        double b = getLongitude() - other.getLongitude();
        return Math.sqrt(a*a + b*b);
    }

}
