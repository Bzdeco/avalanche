package las2etin.model;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.io.Serializable;
import java.util.Objects;

public class TerrainCell implements Serializable
{
    private static final long serialVersionUID = -5616958564343992038L;

    private final Coordinates coordinates;
    private final GeographicCoordinates geographicCoords;
    private final Vector3D normal;
    private final double aspect;
    private final double grade;
    private final double slope;
    private final double planCurvature;
    private final double profileCurvature;

    TerrainCell(Coordinates coordinates,
                GeographicCoordinates geographicCoords,
                Vector3D normal,
                double aspect,
                double grade,
                double slope,
                double planCurvature,
                double profileCurvature)
    {
        this.coordinates = coordinates;
        this.geographicCoords = geographicCoords;
        this.normal = normal;
        this.aspect = aspect;
        this.grade = grade;
        this.slope = slope;
        this.planCurvature = planCurvature;
        this.profileCurvature = profileCurvature;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

	public GeographicCoordinates getGeographicCoords()
	{
		return geographicCoords;
	}

	public int getX()
    {
        return coordinates.getX();
    }

    public int getY()
    {
        return coordinates.getY();
    }

    public Vector3D getNormal()
    {
        return normal;
    }

    public double getAspect()
    {
        return aspect;
    }

    public double getGrade()
    {
        return grade;
    }

    public double getSlope()
    {
        return slope;
    }

    public double getPlanCurvature()
    {
        return planCurvature;
    }

    public double getProfileCurvature()
    {
        return profileCurvature;
    }

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TerrainCell that = (TerrainCell) o;
		return Double.compare(that.aspect, aspect) == 0 &&
				Double.compare(that.grade, grade) == 0 &&
				Double.compare(that.slope, slope) == 0 &&
				Double.compare(that.planCurvature, planCurvature) == 0 &&
				Double.compare(that.profileCurvature, profileCurvature) == 0 &&
				Objects.equals(coordinates, that.coordinates) &&
				Objects.equals(geographicCoords, that.geographicCoords) &&
				Objects.equals(normal, that.normal);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(coordinates, geographicCoords, normal, aspect, grade, slope, planCurvature, profileCurvature);
	}
}
