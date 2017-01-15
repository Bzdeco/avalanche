package backend.rasterizer.tasks;

import backend.rasterizer.RiskProps;
import backend.rasterizer.TerrainProps;
import backend.rasterizer.Utils;
import dto.WeatherDto;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.e175.klaus.solarpositioning.*;

import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AvalancheRisk extends Service<float[][][]> {
    private WeatherDto weather;
    public WeatherDto getWeather() { return weather; }
    public void setWeather(WeatherDto weather) {
        this.weather = weather;
    }

    private Future<float[][][]> fterrain;

    public AvalancheRisk(Future<float[][][]> terrain) {
        fterrain = terrain;
    }

    @Override
    protected Task<float[][][]> createTask() {
        return new Task<float[][][]>() {
            @Override
            public float[][][] call() throws ExecutionException, InterruptedException {
                // Hillshade calculation according to current weather
                float ambient = 0.25f;

                GregorianCalendar dateTime = new GregorianCalendar();
                dateTime.setTime(weather.getTime());

                AzimuthZenithAngle position = Grena3.calculateSolarPosition(
                        dateTime,
                        49.232528, // latitude (degrees)
                        19.981833, // longitude (degrees)
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
                float[][][] terrain = fterrain.get();

                int nRows = terrain.length;
                int nCols = terrain[0].length;

                float results[][][] = new float[nRows][nCols][3];

                for (int iRow = 0; iRow < nRows; iRow++) {
                    for (int iCol = 0; iCol < nCols; iCol++) {
                        final float[] t = terrain[iRow][iCol];
                        float[] r = results[iRow][iCol];

                        float cosTheta = (float)Math.max(0, t[TerrainProps.NORMALX] * xSun
                                + t[TerrainProps.NORMALY] * ySun
                                + t[TerrainProps.NORMALZ] * zSun);
                        float hillshade = Utils.clamp(0, cosTheta * directLight + ambient, 1);

                        r[RiskProps.HILLSHADE] = hillshade;


                        r[RiskProps.RISK] = 1f;
                    }
                }

                return results;
            }
        };
    }
}
