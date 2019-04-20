package las2etin.model;

import java.io.Serializable;
import java.util.Objects;

public class GeographicCoordinates implements Serializable
{
	private final float latitude;
	private final float longitude;

	public GeographicCoordinates(final float latitude, final float longitude)
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
