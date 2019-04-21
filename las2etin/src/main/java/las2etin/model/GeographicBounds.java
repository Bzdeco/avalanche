package las2etin.model;

import java.io.Serializable;
import java.util.Objects;

public class GeographicBounds implements Serializable
{
	private final float minLatitude;
	private final float maxLatitude;
	private final float minLongitude;
	private final float maxLongitude;
	private final GeographicCoordinates centerCoords;

	public GeographicBounds(float minLatitude, float maxLatitude, float minLongitude, float maxLongitude)
	{
		this.minLatitude = minLatitude;
		this.maxLatitude = maxLatitude;
		this.minLongitude = minLongitude;
		this.maxLongitude = maxLongitude;
		centerCoords = new GeographicCoordinates((minLatitude + maxLatitude) / 2,
										(minLongitude + maxLongitude) / 2);
	}

	public float getMinLatitude()
	{
		return minLatitude;
	}

	public float getMaxLatitude()
	{
		return maxLatitude;
	}

	public float getMinLongitude()
	{
		return minLongitude;
	}

	public float getMaxLongitude()
	{
		return maxLongitude;
	}

	public GeographicCoordinates getCenterCoords()
	{
		return centerCoords;
	}

	public void setCenterAltitude(double altitude)
	{
		centerCoords.setAltitude(altitude);
	}

	public float getWidth()
	{
		return maxLongitude - minLongitude;
	}

	public float getHeight()
	{
		return maxLatitude - minLatitude;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GeographicBounds that = (GeographicBounds) o;
		return Float.compare(that.minLatitude, minLatitude) == 0 &&
				Float.compare(that.maxLatitude, maxLatitude) == 0 &&
				Float.compare(that.minLongitude, minLongitude) == 0 &&
				Float.compare(that.maxLongitude, maxLongitude) == 0 &&
				Objects.equals(centerCoords, that.centerCoords);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(minLatitude, maxLatitude, minLongitude, maxLongitude, centerCoords);
	}
}
