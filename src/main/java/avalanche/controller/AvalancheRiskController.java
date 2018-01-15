package avalanche.controller;

import avalanche.model.database.WeatherConnector;
import avalanche.model.database.WeatherDto;
import avalanche.model.risk.Risk;
import avalanche.model.risk.RiskCell;
import javafx.scene.control.TableView;
import las2etin.model.Terrain;
import las2etin.model.TerrainCell;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import weatherCollector.coordinates.Coords;
import com.sun.javafx.util.Utils;
import net.e175.klaus.solarpositioning.AzimuthZenithAngle;
import net.e175.klaus.solarpositioning.DeltaT;
import net.e175.klaus.solarpositioning.Grena3;

import java.util.*;

public class AvalancheRiskController {

    private static final Logger LOGGER = LogManager.getLogger();

    private Risk risk;
    private Terrain terrain;
    private Coords geographicalCoordinates;

    public AvalancheRiskController(final Terrain terrain, final Coords geographicalCoordinates)
    {
        this.risk = new Risk(terrain);
        this.terrain = terrain;
        this.geographicalCoordinates = geographicalCoordinates;
    }

    public float getGlobalRiskValue()
    {
        return risk.getGlobalRiskValue();
    }

    public Risk getEvaluatedRisk(List<WeatherDto> weatherConditions)
    {
        LOGGER.info("Risk evaluation started");
        risk.predictGlobalRiskValue(weatherConditions);
        risk.predictLocalRisks();
        //oldRiskPrediction(weatherConditions);
        LOGGER.info("Risk evaluation finished successfully");

        return risk;
    }

    public List<WeatherDto> fetchWeatherDataInto(TableView tableView)
    {
        WeatherConnector connector = WeatherConnector.getInstance();
        connector.setTableView(tableView);
        return connector.fetchAndBuildData();
    }

    private void oldRiskPrediction(List<WeatherDto> weatherList)
    {
        for (WeatherDto weather : weatherList) {

            GregorianCalendar dateTime = new GregorianCalendar();
            dateTime.setTime(weather.getTime());
            Float latitude = geographicalCoordinates.getLatitude();
            Float longitude = geographicalCoordinates.getLongitude();
            AzimuthZenithAngle solarPosition = Grena3.calculateSolarPosition(
                    dateTime,
                    latitude,
                    longitude,
                    DeltaT.estimate(dateTime));

            double sunAzimuth = Math.toRadians(solarPosition.getAzimuth());
            double sunZenith = Math.toRadians(solarPosition.getZenithAngle());
            float ambient = 0.25f;
            float directLight = 1f - ambient;
            double cosA = Math.cos(sunAzimuth);
            double sinA = Math.sin(sunAzimuth);
            double cosE = Math.sin(sunZenith); // Inverted (sin, cos) because we want elevation
            double sinE = Math.cos(sunZenith);
            double xSun = cosA * cosE;
            double ySun = sinA * cosE;
            double zSun = sinE;

            // Risk calculation and update
            for (List<TerrainCell> terrainCellsList : terrain.getTerrainCells().values()) {
                for (TerrainCell terrainCell : terrainCellsList) {
                    float riskValue = 0f;

                    Vector3D terrainCellNormal = terrainCell.getNormal();
                    float cosTheta = (float) Math.max(0, terrainCellNormal.getX() * xSun
                            + terrainCellNormal.getY() * ySun + terrainCellNormal.getZ() * zSun);

                    float hillshade = Utils.clamp(0, cosTheta * directLight + ambient, 1);

                    if (weather.getWind_speed() > 5)
                        riskValue += 0.2;
                    else if (weather.getWind_speed() > 2)
                        riskValue += 0.1;

                    if (weather.getSnow() > 0.01)
                        riskValue += 0.2;

                    if (terrainCell.getProfileCurvature() > 1E-3) {
                        riskValue += 0.5; //teren wklęsły
                        if (terrainCell.getProfileCurvature() > 1E-2)
                            riskValue += 0.2; //teren bardzo wklesly
                    } else if (terrainCell.getProfileCurvature() < -1E-3) {
                        riskValue += 0.2; //teren wypukły
                        if (terrainCell.getProfileCurvature() > 1E-2)
                            riskValue += 0.2; //bardziej wypukły
                    } else {
                        riskValue = 0; //teren w przybliżeniu płaski
                    }

                    if (terrainCell.getPlanCurvature() > 1E-3) {
                        riskValue += 0.5;//żleby
                        if (terrainCell.getPlanCurvature() > 1E-2)
                            riskValue += 0.2;//strome żleby
                    } else if (terrainCell.getPlanCurvature() < -1E-3) {
                        riskValue += 0.2;//grzędy
                        if (terrainCell.getPlanCurvature() > 1E-2)
                            riskValue += 0.2;
                    } else {
                        riskValue = 0;
                    }

                    if (terrainCell.getGrade() < Math.toRadians(25) || terrainCell.getGrade() > Math.toRadians(60))
                        riskValue = 0;
                    if (weather.getSnow() < 0.001)
                        riskValue = 0;


                    Optional<RiskCell> riskCellOpt = risk.getRiskCellWithCoordinates(terrainCell.getCoordinates());
                    if(riskCellOpt.isPresent()) {
                        RiskCell riskCell = riskCellOpt.get();
                        if (riskCell.getRiskValue() < riskValue)
                            riskCell.setRiskValue(riskValue);
                    }
                }
            }
        }
    }
}
