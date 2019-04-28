package weatherCollector.coordinates;

import java.util.Objects;

public class Coords
{
    private final float latitude;
    private final float longitude;
    private float elevation;

    public Coords(final float latitude, final float longitude)
    {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float getLatitude()
    {
        return latitude;
    }

    public float getLongitude()
    {
        return longitude;
    }

    public float getElevation()
    {
        return elevation;
    }

    public void setElevation(float value){
        elevation = value;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Coords coords = (Coords) o;
        return Float.compare(coords.latitude, latitude) == 0 &&
                Float.compare(coords.longitude, longitude) == 0;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString()
    {
        return "Coords{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
