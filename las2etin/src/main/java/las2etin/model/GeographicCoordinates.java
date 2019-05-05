package las2etin.model;

import lombok.Data;

import java.io.Serializable;

@Data
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
}
