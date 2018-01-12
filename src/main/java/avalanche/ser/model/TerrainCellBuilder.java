package avalanche.ser.model;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import tinfour.common.Vertex;
import tinfour.gwr.BandwidthSelectionMethod;
import tinfour.gwr.SurfaceModel;
import tinfour.interpolation.GwrTinInterpolator;

import static com.google.common.base.Preconditions.checkNotNull;

public class TerrainCellBuilder
{
    private static final SurfaceModel SURFACE_MODEL = SurfaceModel.CubicWithCrossTerms;
    private static final BandwidthSelectionMethod BANDWIDTH_SELECTION_METHOD = BandwidthSelectionMethod
            .FixedProportionalBandwidth; // FIXME should it be different?
    private static final double INTERPOLATION_BANDWIDTH = 100; // FIXME arbitrary value

    private GwrTinInterpolator interpolator;
    private Vertex vertex;
    private Coordinates coordinates;

    public TerrainCellBuilder()
    {
    }

    public TerrainCellBuilder withInterpolator(GwrTinInterpolator interpolator)
    {
        this.interpolator = interpolator;
        return this;
    }

    public TerrainCellBuilder withVertex(Vertex interpolatedVertex)
    {
        this.vertex = interpolatedVertex;
        return this;
    }

    public TerrainCellBuilder withCoordinates(Coordinates coordinates)
    {
        this.coordinates = coordinates;
        return this;
    }

    public TerrainCell build()
    {
        checkNotNull(interpolator);
        checkNotNull(vertex);
        checkNotNull(coordinates);

        double[] coefficients = interpolator.getCoefficients();

        double altitude = getAltitude();
        Vector3D normal = calculateNormal();

        if (isCoefficientsAvailable(coefficients)) {
            double aspect = calculateAspect(coefficients);
            double grade = calculateGrade(coefficients);
            double slope = calculateSlope(grade);
            double planCurvature = calculatePlanCurvature(coefficients);
            double profileCurvature = calculateProfileCurvature(coefficients);

            return new TerrainCell(coordinates,
                                   altitude,
                                   normal,
                                   aspect,
                                   grade,
                                   slope,
                                   planCurvature,
                                   profileCurvature);
        }
        else {
            return new TerrainCell(coordinates,
                                   altitude,
                                   normal,
                                   0,
                                   0,
                                   0,
                                   0,
                                   0);
        }
    }

    private boolean isCoefficientsAvailable(double[] coefficients)
    {
        return coefficients.length > 0;
    }

    private double getAltitude()
    {
        return interpolator.interpolate(SURFACE_MODEL,
                                        BANDWIDTH_SELECTION_METHOD,
                                        INTERPOLATION_BANDWIDTH,
                                        vertex.x,
                                        vertex.y,
                                        null); // RIP
    }

    private Vector3D calculateNormal()
    {
        double[] doubleCoordinates = interpolator.getSurfaceNormal();
        double normalX = doubleCoordinates[0];
        double normalY = doubleCoordinates[1];
        double normalZ = doubleCoordinates[2];

        return new Vector3D(normalX, normalY, normalZ);
    }

    private double calculateAspect(double[] coefficients)
    {
        double zdx = coefficients[1];
        double zdy = coefficients[2];

        return Math.atan2(zdy, zdx);
    }

    private double calculateGrade(double[] coefficients)
    {
        double zdx = coefficients[1];
        double zdy = coefficients[2];

        return Math.sqrt(Math.pow(zdx, 2) + Math.pow(zdy, 2));
    }

    private double calculateSlope(double grade)
    {
        return Math.toDegrees(Math.atan(grade));
    }

    private double calculatePlanCurvature(double[] coefficients)
    {
        double zdx = coefficients[1];
        double zdy = coefficients[2];
        double zdxdx = 2 * coefficients[3];
        double zdydy = 2 * coefficients[4];
        double zdxdy = coefficients[5];

        return -(zdxdx * Math.pow(zdy, 2) - 2 * zdxdy * zdx * zdy + zdydy * Math.pow(zdx, 2)) / (Math.pow(zdx, 2) +
                Math.pow(zdy, 2));
    }

    private double calculateProfileCurvature(double[] coefficients)
    {
        double zdx = coefficients[1];
        double zdy = coefficients[2];
        double zdxdx = 2 * coefficients[3];
        double zdydy = 2 * coefficients[4];
        double zdxdy = coefficients[5];

        return -(zdxdx * Math.pow(zdx, 2) + 2 * zdxdy * zdx * zdy + zdydy * Math.pow(zdy, 2)) / ((Math.pow(zdx, 2) +
                Math.pow(zdy, 2)) * Math.pow(Math.pow(zdx, 2) + Math.pow(zdy, 2) + 1, 3/2));
    }
}