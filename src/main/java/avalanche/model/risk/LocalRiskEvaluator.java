package avalanche.model.risk;

import avalanche.model.database.WeatherDto;
import las2etin.model.TerrainCell;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.List;

public class LocalRiskEvaluator
{
    // Parameters determining value for which risk will be increased(INC)/decreased(DEC) when a particular local
    // characteristic is detected
    private static final float SLOPE_INC = 0.5f;
    private static final float PROFILE_CURV_DEC = 0.5f;
    private static final float PROFILE_CURV_INC = 0.5f;
    private static final float PLAN_CURV_INC = 0.5f;
    private static final float SNOW_ACCUMULATION_INC = 0.15f;
    private static final float SNOW_DEPLETION_DEC = 0.3f;

    private final List<WeatherDto> weatherConditions;

    public LocalRiskEvaluator(List<WeatherDto> weatherConditions)
    {
        this.weatherConditions = weatherConditions;
    }

    /**
     * Here normalized risk value is evaluated for a given small terrain area.
     */
    public float evaluate(TerrainCell terrainCell)
    {
        float riskValue = 0f;
        float maxRiskValue = 7.5f; // maximum possible risk value for a given point

        if (isSlopeInRange(terrainCell.getSlope())) {
            riskValue += SLOPE_INC;
            riskValue += applyCurvatureEffect(terrainCell);
            riskValue += applyWindDirection(terrainCell);
        }

        // don't allow negative risk values
        if (riskValue < 0) riskValue = 0;

        return riskValue / maxRiskValue;
    }

    /**
     * Only slopes between 30 and 45 degrees are concerned as they are most likely to produce deep slab avalanches.
     */
    private boolean isSlopeInRange(double slope)
    {
        return slope >= 30 && slope <= 45;
    }

    /**
     * Profile and plan curvature in a given point can increase or decrease avalanche risk.
     */
    private float applyCurvatureEffect(TerrainCell terrainCell)
    {
        float riskValue = 0f;

        // Surface upwardly convex
        if (terrainCell.getProfileCurvature() < 0)
            riskValue -= PROFILE_CURV_DEC;
        // Surface upwardly concave
        else
            riskValue += PROFILE_CURV_INC;

        // Surface sidewardly concave
        if (terrainCell.getPlanCurvature() < 0)
            riskValue += PLAN_CURV_INC;

        return riskValue;
    }

    /**
     * This function applies wind direction aspect to risk evaluation. Wind direction can determine how much snow is
     * accumulated and blown down the hill. We can determine how is a given point affected by wind taking this point
     * normal vector's azimuth and wind direction. Based on the difference between these two values it can be
     * determined if the snow is accumulated or blown down the hill.
     */
    private float applyWindDirection(TerrainCell terrainCell)
    {
        // Calculating normal vector's azimuth
        Vector3D normal = terrainCell.getNormal();
        double vectorAzimuthInDegrees = Math.toDegrees(normal.getAlpha());
        if (vectorAzimuthInDegrees < 0)
            vectorAzimuthInDegrees = 360 + vectorAzimuthInDegrees;

        // Calculating how each weather forecast affects snow accumulation
        float riskInc = 0f;
        for (WeatherDto weatherMeasurement : weatherConditions)
        {
            Float windDegree = weatherMeasurement.getWind_deg();
            double angleDifference = Math.abs(vectorAzimuthInDegrees - windDegree);

            // Snow accumulated
            if ((angleDifference >= 75 && angleDifference <= 95) || (angleDifference >= 265 && angleDifference <= 285))
                riskInc += SNOW_ACCUMULATION_INC;

            // Snow blown down
            if (angleDifference >= 135 && angleDifference <= 225)
                riskInc -= SNOW_DEPLETION_DEC;
        }

        return riskInc;
    }
}
