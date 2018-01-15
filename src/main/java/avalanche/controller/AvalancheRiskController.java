package avalanche.controller;

import avalanche.model.database.WeatherDto;
import avalanche.model.risk.Risk;
import avalanche.model.risk.RiskCell;
import avalanche.model.risk.LocalRiskEvaluator;
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
import java.util.stream.Collectors;

public class AvalancheRiskController {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final int NUMBER_OF_MEASUREMENTS = 40;
    private static final int NUMBER_OF_MEASUREMENT_DAYS = 5;
    private static final int NUMBER_OF_MEASUREMENTS_PER_DAY = 8;
    private static final int SNOW_PRECIPITATION_THRESHOLD_IN_MM = 100;
    private static final int MAX_WIND_DEGREE_CHANGE = 90;
    private static final int PER_DAY_FLUCTUATION_THRESHOLD = 10;
    private static final float LOW_TEMPERATURE_INDICATOR = -10f;
    private static final float LOW_TEMPERATURE_MIN_DAYS_THRESHOLD = 2.5f;

    private List<WeatherDto> weatherList = new LinkedList<>();
    private Risk risk;
    private Terrain terrain;
    private Coords terrainCoords;

    public void prepareAvalanchePrediction(final Terrain terrain, final Coords terrainCoords) {
        this.risk = new Risk(terrain);
        this.terrain = terrain;
        this.terrainCoords = terrainCoords;
    }

    public void addWeather(final List<WeatherDto> weatherDtoList) {
        weatherList.addAll(weatherDtoList);
    }

    public void predict() {

        LOGGER.info("Prediction started");
        float globalRiskValue = predictGlobalRiskValue();
        risk.updateGlobalRiskValue(globalRiskValue);
        predictLocalRisks();

        for (WeatherDto weather : weatherList) {

            GregorianCalendar dateTime = new GregorianCalendar();
            dateTime.setTime(weather.getTime());
            Float latitude = terrainCoords.getLatitude();
            Float longitude = terrainCoords.getLongitude();
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


                    RiskCell riskCell = risk.getRiskCellWithCoordinates(terrainCell.getCoordinates());
                    if (riskCell.getRiskValue() < riskValue) riskCell.setRiskValue(riskValue);
                }
            }
        }

        LOGGER.info("Prediction finished successfully");
    }

    public void predictLocalRisks()
    {
        LocalRiskEvaluator localRiskEvaluator = new LocalRiskEvaluator();
        Collection<List<RiskCell>> allRiskCells = risk.getRiskCells().values();
        allRiskCells.forEach(row -> row.forEach(riskCell -> riskCell.evaluateLocalRisk(localRiskEvaluator)));
    }

    public float predictGlobalRiskValue()
    {
        List<Float> temperatureMeasurements = weatherList.stream()
                                                         .map(WeatherDto::getTemp)
                                                         .collect(Collectors.toList());

        float globalRiskValue = 0;
        if (isTemperatureWithIncreasingTendency(temperatureMeasurements)) {
            LOGGER.info("Temperature increasing tendency");
            globalRiskValue += 2;
        }
        if (isHighPrecipitationIntensity()) {
            LOGGER.info("High precipitation intensity");
            globalRiskValue += 2;
        }
        if (isWindDirectionChangeLow()) {
            LOGGER.info("Low wind direction change");
            globalRiskValue += 1;
        }
        if (isLongLastingLowTemperature(temperatureMeasurements)) {
            LOGGER.info("Long lasting low temperature");
            globalRiskValue += 1;
        }
        if (isHighTemperatureFluctuation(temperatureMeasurements)) {
            LOGGER.info("High temperature fluctuations");
            globalRiskValue += 1;
        }

        float normalizedGlobalRisk = globalRiskValue / 7;
        LOGGER.info("Global risk value predicted: {}", normalizedGlobalRisk);
        return normalizedGlobalRisk;
    }

    private boolean isTemperatureWithIncreasingTendency(List<Float> temperatureMeasurements)
    {
        List<Float> temperatureIntervals = calculateIntervals(temperatureMeasurements);

        double totalChange = temperatureIntervals.stream().mapToDouble(Float::floatValue).sum();
        float lastTempMeasurement = temperatureMeasurements.get(temperatureMeasurements.size() - 1);

        boolean isTotalChangePositive =
                totalChange > 0 && isLastTempInTemperatureZone(lastTempMeasurement);
        boolean isLongWarmingPeriod =
                calculateLongestIncreaseLength(temperatureIntervals) >= NUMBER_OF_MEASUREMENTS_PER_DAY;

        return isTotalChangePositive || isLongWarmingPeriod;
    }

    private List<Float> calculateIntervals(List<Float> temperatureMeasurements)
    {
        List<Float> temperatureIntervals = new ArrayList<>();
        for(int measurementCount = 0; measurementCount < temperatureMeasurements.size() - 1; measurementCount++)
        {
            float currentTempValue = temperatureMeasurements.get(measurementCount);
            float nextTempValue = temperatureMeasurements.get(measurementCount + 1);
            temperatureIntervals.add(nextTempValue - currentTempValue);
        }
        return temperatureIntervals;
    }

    private int calculateLongestIncreaseLength(List<Float> temperatureIntervals)
    {
        int longestIncreaseLength = 0;
        int tempLongestIncreaseLength = 0;
        for(float interval : temperatureIntervals)
        {
            if (interval > 0)
                tempLongestIncreaseLength++;
            else
            {
                if (tempLongestIncreaseLength > longestIncreaseLength)
                    longestIncreaseLength = tempLongestIncreaseLength;
                tempLongestIncreaseLength = 0;
            }
        }

        return longestIncreaseLength;
    }

    private boolean isLastTempInTemperatureZone(float lastTempMeasurement)
    {
        return lastTempMeasurement >= -10f && lastTempMeasurement <= 0;
    }

    private boolean isHighPrecipitationIntensity()
    {
        double snowPrecipitationSum = weatherList.stream()
                                    .map(WeatherDto::getSnow)
                                    .mapToDouble(Float::floatValue).sum();
        double averageDayPrecipitationSum = snowPrecipitationSum / NUMBER_OF_MEASUREMENT_DAYS;

        return averageDayPrecipitationSum >= SNOW_PRECIPITATION_THRESHOLD_IN_MM;
    }

    private boolean isWindDirectionChangeLow()
    {
        List<Float> windDirections = weatherList.stream().map(WeatherDto::getWind_deg).collect(Collectors.toList());
        Collections.sort(windDirections);

        float minDirectionDegree = windDirections.get(0);
        float maxDirectionDegree = windDirections.get(windDirections.size()-1);

        return Math.abs(maxDirectionDegree - minDirectionDegree) < MAX_WIND_DEGREE_CHANGE;
    }

    private boolean isLongLastingLowTemperature(List<Float> temperatureMeasurements)
    {
        int longestLowTempPeriod = 0;
        int tempLongestLowTempPeriod = 0;
        for (float temperature : temperatureMeasurements)
        {
            if (temperature < LOW_TEMPERATURE_INDICATOR)
                tempLongestLowTempPeriod++;
            else
            {
                if (tempLongestLowTempPeriod > longestLowTempPeriod)
                    longestLowTempPeriod = tempLongestLowTempPeriod;
                tempLongestLowTempPeriod = 0;
            }
        }
        float lowTempDays = longestLowTempPeriod / NUMBER_OF_MEASUREMENTS_PER_DAY;

        return lowTempDays >= LOW_TEMPERATURE_MIN_DAYS_THRESHOLD;
    }

    private boolean isHighTemperatureFluctuation(List<Float> temperatureMeasurements)
    {
        List<List<Float>> perDayTemperatures = getPerDayTemperatures(temperatureMeasurements);
        List<Float> perDayFluctuations = getPerDayFluctuations(perDayTemperatures);
        List<Boolean> perDayFluctuationsInTempRange = perDayTemperatures.stream()
                                                                    .map(this::isDayTemperaturesViable)
                                                                    .collect(Collectors.toList());

        boolean isHighTemperatureFluctuation = false;
        for (int i = 0; i < perDayFluctuations.size() - 1; i++)
        {
            float dayTempFluctuation = perDayFluctuations.get(i);
            boolean isDayTempFluctuationInRange = perDayFluctuationsInTempRange.get(i);

            if (isDayTempFluctuationInRange && dayTempFluctuation > PER_DAY_FLUCTUATION_THRESHOLD)
                isHighTemperatureFluctuation = true;

        }

        return isHighTemperatureFluctuation;
    }

    private boolean isDayTemperaturesViable(List<Float> temperatures)
    {
        Collections.sort(temperatures);
        float minTemp = temperatures.get(0);
        float maxTemp = temperatures.get(0);

        return minTemp < 0 && maxTemp >= 0;
    }

    private List<List<Float>> getPerDayTemperatures(List<Float> temperatures)
    {
        List<List<Float>> perDayTemperatures = new ArrayList<>();
        for(int i = 0; i < temperatures.size(); i += NUMBER_OF_MEASUREMENTS_PER_DAY)
            perDayTemperatures.add(temperatures.subList(
                    i, Math.min(i + NUMBER_OF_MEASUREMENTS_PER_DAY, temperatures.size())));

        return perDayTemperatures;
    }

    private List<Float> getPerDayFluctuations(List<List<Float>> perDayTemperatures)
    {
        return perDayTemperatures.stream()
                                 .map(this::calculateFluctuationValue)
                                 .collect(Collectors.toList());
    }

    private float calculateFluctuationValue(List<Float> temperatures)
    {
        Collections.sort(temperatures);
        float minTemp = temperatures.get(0);
        float maxTemp = temperatures.get(temperatures.size() - 1);

        return Math.abs(maxTemp - minTemp);
    }

}
