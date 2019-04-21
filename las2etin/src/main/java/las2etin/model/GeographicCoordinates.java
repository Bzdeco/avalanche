package las2etin.model;

import java.io.Serializable;
import java.util.Objects;

public class GeographicCoordinates implements Serializable
{
	private final float latitude;
	private final float longitude;
	private double altitude;

	public GeographicCoordinates(float latitude, float longitude, double altitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}

	public GeographicCoordinates(final float latitude, final float longitude)
	{
		this(latitude, longitude, 0);
	}

	public float getLatitude()
	{
		return latitude;
	}

	public float getLongitude()
	{
		return longitude;
	}

	public double getAltitude()
	{
		return altitude;
	}

	public void setAltitude(double altitude)
	{
		this.altitude = altitude;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GeographicCoordinates that = (GeographicCoordinates) o;
		return Float.compare(that.latitude, latitude) == 0 &&
				Float.compare(that.longitude, longitude) == 0;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(latitude, longitude);
	}

	@Override
	public String toString()
	{
		return "GeographicCoordinates{" +
				"latitude=" + latitude +
				", longitude=" + longitude +
				'}';
	}
}
