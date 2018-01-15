package avalanche.model.risk;

import avalanche.model.database.WeatherDto;
import las2etin.model.Coordinates;
import las2etin.model.Terrain;
import las2etin.model.TerrainCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by annterina on 13.01.18.
 */
public class Risk {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final int NUMBER_OF_MEASUREMENTS = 40;
    private static final int NUMBER_OF_MEASUREMENT_DAYS = 5;
    private static final int NUMBER_OF_MEASUREMENTS_PER_DAY = 8;
    private static final int SNOW_PRECIPITATION_THRESHOLD_IN_CM = 10;
    private static final int MAX_WIND_DEGREE_CHANGE = 90;
    private static final int PER_DAY_FLUCTUATION_THRESHOLD = 10;
    private static final float LOW_TEMPERATURE_INDICATOR = -10f;
    private static final float LOW_TEMPERATURE_MIN_DAYS_THRESHOLD = 2.5f;

    private float globalRiskValue;
    private Map<Integer, List<RiskCell>> riskCells = new HashMap<>();

    public Risk(Terrain terrain) {
        for (Map.Entry<Integer, List<TerrainCell>> entry : terrain.getTerrainCells().entrySet()) {
            List<RiskCell> riskCellsRow = new ArrayList<>();
            for (TerrainCell terrainCell: entry.getValue()) {
                riskCellsRow.add(new RiskCell(terrainCell));
            }
            riskCells.put(entry.getKey(), riskCellsRow);
        }
    }

    public Optional<RiskCell> getRiskCellWithCoordinates(Coordinates coordinates)
    {
        int x = coordinates.getX();
        int y = coordinates.getY();

        List<RiskCell> searchedRow = riskCells.getOrDefault(x, new ArrayList<>());
        if (isColumnPresentInRow(y, searchedRow)) {
            return Optional.of(searchedRow.get(y));
        }
        else {
            return Optional.empty();
        }
    }

    private boolean isColumnPresentInRow(int columnIndex, List<RiskCell> searchedRow)
    {
        return columnIndex <= searchedRow.size();
    }

    public Map<Integer, List<RiskCell>> getRiskCells()
    {
        return riskCells;
    }

    public String getAllRiskValues() {
        String values = "";
        for (Map.Entry<Integer, List<RiskCell>> entry : riskCells.entrySet()) {
            for (RiskCell riskCell : entry.getValue()) {
                values += riskCell.getRiskValue() + " ";
            }
            values += "\n";
        }
        return values;
    }

    public float getGlobalRiskValue()
    {
        return globalRiskValue;
    }

    public void predictLocalRisks()
    {
        LocalRiskEvaluator localRiskEvaluator = new LocalRiskEvaluator();
        Collection<List<RiskCell>> allRiskCells = getRiskCells().values();
        allRiskCells.forEach(row -> row.forEach(riskCell -> riskCell.evaluateLocalRisk(localRiskEvaluator)));
    }

    public void predictGlobalRiskValue(List<WeatherDto> weatherConditions)
    {
        List<Float> temperatureMeasurements = weatherConditions.stream()
                                                               .map(WeatherDto::getTemp)
                                                               .collect(Collectors.toList());

        float globalRiskValue = 0;
        if (isTemperatureWithIncreasingTendency(temperatureMeasurements)) {
            LOGGER.info("Temperature increasing tendency");
            globalRiskValue += 2;
        }
        if (isHighPrecipitationIntensity(weatherConditions)) {
            LOGGER.info("High precipitation intensity");
            globalRiskValue += 2;
        }
        if (isWindDirectionChangeLow(weatherConditions)) {
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

        this.globalRiskValue = normalizedGlobalRisk;
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

    private boolean isHighPrecipitationIntensity(List<WeatherDto> weatherConditions)
    {
        double snowPrecipitationSum = weatherConditions.stream()
                                                       .map(WeatherDto::getSnow)
                                                       .mapToDouble(Float::floatValue).sum();
        double averageDayPrecipitationSum = snowPrecipitationSum / NUMBER_OF_MEASUREMENT_DAYS;

        return averageDayPrecipitationSum >= SNOW_PRECIPITATION_THRESHOLD_IN_CM;
    }

    private boolean isWindDirectionChangeLow(List<WeatherDto> weatherConditions)
    {
        List<Float> windDirections = weatherConditions.stream()
                                                      .map(WeatherDto::getWind_deg)
                                                      .collect(Collectors.toList());
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
