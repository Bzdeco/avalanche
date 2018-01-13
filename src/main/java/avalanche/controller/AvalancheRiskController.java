package avalanche.controller;

import avalanche.model.LeData;
import avalanche.model.database.WeatherDto;
import avalanche.view.layers.LayerUI;
import avalanche.view.layers.magicalindexes.RiskProps;
import avalanche.view.layers.magicalindexes.TerrainProps;
import weatherCollector.coordinates.Coords;
import com.sun.javafx.util.Utils;
import javafx.concurrent.Task;
import net.e175.klaus.solarpositioning.AzimuthZenithAngle;
import net.e175.klaus.solarpositioning.DeltaT;
import net.e175.klaus.solarpositioning.Grena3;
import weatherCollector.coordinates.StaticMapNameToCoordsConverter;

import java.util.GregorianCalendar;
import java.util.concurrent.ExecutorService;

public class AvalancheRiskController
{
    public final StaticMapNameToCoordsConverter converter = new StaticMapNameToCoordsConverter();

    private WeatherDto weather;
    private Task<LeData> task;

    public void prepareAvalanchePredictionTask(final LeData data,
                                               final LayerUI avalancheRiskLayer,
                                               final LayerUI hillShadeLayer) {
        task = new Task<LeData>()
        {
            @Override
            protected LeData call() throws Exception
            {
                return predict(data);
            }
        };
        bindUI(avalancheRiskLayer, hillShadeLayer);
    }

    private String extractName(String filename)
    {
        return filename.substring(0, filename.length() - 6);
    }

    private LeData predict(final LeData data) throws Exception
    {
        // Hillshade calculation according to current weather
        float ambient = 0.25f;

        GregorianCalendar dateTime = new GregorianCalendar();
        dateTime.setTime(weather.getTime());

        final Coords coords = converter.convert(extractName(System.getProperty("filename")));

        Float latitude = coords.getLatitude();
        Float longitude = coords.getLongitude();

        AzimuthZenithAngle position = Grena3.calculateSolarPosition(
                dateTime,
                latitude,
                longitude,
                DeltaT.estimate(dateTime)); // avg. air temperature (°C)

        double sunAzimuth = Math.toRadians(position.getAzimuth());
        double sunZenith = Math.toRadians(position.getZenithAngle());

        float directLight = 1f - ambient;
        double cosA = Math.cos(sunAzimuth);
        double sinA = Math.sin(sunAzimuth);
        double cosE = Math.sin(sunZenith); // Inverted (sin, cos) because we want elevation
        double sinE = Math.cos(sunZenith);
        double xSun = cosA * cosE;
        double ySun = sinA * cosE;
        double zSun = sinE;

        // Risk calculation
        float[][][] terrain = data.getData();

        int nRows = terrain.length;
        int nCols = terrain[0].length;

        float results[][][] = new float[nRows][nCols][3];

        for (int iRow = 0; iRow < nRows; iRow++) {
            for (int iCol = 0; iCol < nCols; iCol++) {
                final float[] t = terrain[iRow][iCol];
                float[] r = results[iRow][iCol];

                float cosTheta = (float) Math.max(0, t[TerrainProps.NORMALX] * xSun
                        + t[TerrainProps.NORMALY] * ySun
                        + t[TerrainProps.NORMALZ] * zSun);
                float hillshade = Utils.clamp(0, cosTheta * directLight + ambient, 1);

                r[RiskProps.HILLSHADE] = hillshade;


                float risk = 1f;

                if (t[TerrainProps.PROFCURV] > 1E-3) {
                    risk += 0.5; //teren wklęsły
                    if (t[TerrainProps.PROFCURV] > 1E-2)
                        risk += 0.2; //teren bardzo wklesly
                } else if (t[TerrainProps.PROFCURV] < -1E-3) {
                    risk += 0.2; //teren wypukły
                    if (t[TerrainProps.PROFCURV] > 1E-2)
                        risk += 0.2; //bardziej wypukły
                } else {
                    risk = 0; //teren w przybliżeniu płaski
                    r[RiskProps.RISK] = risk;
                    continue;
                }

                if (t[TerrainProps.PLANCURV] > 1E-3) {
                    risk += 0.5;//żleby
                    if (t[TerrainProps.PLANCURV] > 1E-2)
                        risk += 0.2;//strome żleby
                } else if (t[TerrainProps.PLANCURV] < -1E-3) {
                    risk += 0.2;//grzędy
                    if (t[TerrainProps.PLANCURV] > 1E-2)
                        risk += 0.2;
                } else {
                    risk = 0;
                    r[RiskProps.RISK] = risk;
                    continue;
                }

                if (weather.getWind_speed() != null && weather.getWind_speed() > 5)
                    risk += 0.1;
                if (weather.getWind_speed() != null && weather.getWind_speed() > 70)
                    risk += 0.2;
                if (weather.getSnow() != null && weather.getSnow() > 100)
                    risk += 0.2;
                if (t[TerrainProps.GRADE] < Math.toRadians(25) || t[TerrainProps.GRADE] > Math.toRadians(60))
                    risk = 0;
                if (weather.getSnow() != null && weather.getSnow() < 20)
                    risk = 0;
                r[RiskProps.RISK] = risk;
            }
        }

        return new LeData(results);
    }

    private void bindUI(final LayerUI avalancheRiskLayer,
                        final LayerUI hillShadeLayer)
    {
        avalancheRiskLayer.dataProperty().bind(task.valueProperty());
        hillShadeLayer.dataProperty().bind(task.valueProperty());
    }

    public void executeTask(final WeatherDto weatherDto,
                            final ExecutorService executorService)
    {
        this.weather = weatherDto;
        executorService.execute(task);
    }
}
