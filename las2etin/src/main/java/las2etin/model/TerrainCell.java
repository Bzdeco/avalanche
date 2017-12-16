package las2etin.model;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class TerrainCell
{
    private final double altitude;
    private final Vector3D normal;
    private final double aspect;
    private final double grade;
    private final double slope;
    private final double planCurvature;
    private final double profileCurvature;

    TerrainCell(double altitude,
                Vector3D normal,
                double aspect,
                double grade,
                double slope,
                double planCurvature,
                double profileCurvature)
    {
        this.altitude = altitude;
        this.normal = normal;
        this.aspect = aspect;
        this.grade = grade;
        this.slope = slope;
        this.planCurvature = planCurvature;
        this.profileCurvature = profileCurvature;
    }

    public double getAltitude()
    {
        return altitude;
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
}
