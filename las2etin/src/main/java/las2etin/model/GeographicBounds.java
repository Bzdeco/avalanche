package las2etin.model;

import lombok.Data;

import java.io.Serializable;

@Data
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
}
