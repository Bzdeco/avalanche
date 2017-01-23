package backend.rasterizer.tasks;

import backend.rasterizer.RiskProps;
import backend.rasterizer.TerrainProps;
import backend.rasterizer.Utils;
import dto.WeatherDto;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.e175.klaus.solarpositioning.AzimuthZenithAngle;
import net.e175.klaus.solarpositioning.DeltaT;
import net.e175.klaus.solarpositioning.Grena3;

import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AvalancheRisk extends Service<float[][][]> {
    private WeatherDto weather;
    private Future<float[][][]> fterrain;

    public AvalancheRisk(Future<float[][][]> terrain) {
        fterrain = terrain;
    }

    public WeatherDto getWeather() {
        return weather;
    }

    public void setWeather(WeatherDto weather) {
        this.weather = weather;
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

                        float cosTheta = (float) Math.max(0, t[TerrainProps.NORMALX] * xSun
                                + t[TerrainProps.NORMALY] * ySun
                                + t[TerrainProps.NORMALZ] * zSun);
                        float hillshade = Utils.clamp(0, cosTheta * directLight + ambient, 1);

                        r[RiskProps.HILLSHADE] = hillshade;


                        float risk = 1f;

                        if (t[TerrainProps.PROFCURV] > 1E-3) {
                            risk += 0.5;   //teren wklęsły
                            if (t[TerrainProps.PROFCURV] > 1E-2)
                                risk += 0.2; //teren bardzo wklesly
                        } else if (t[TerrainProps.PROFCURV] < -1E-3) {
                            risk += 0.2;//teren wypukły
                            if (t[TerrainProps.PROFCURV] > 1E-2)
                                risk += 0.2;//bardziej wypukły
                        } else{
                            risk = 0;//teren w przybliżeniu płaski
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
                        } else{
                            risk = 0;
                            r[RiskProps.RISK] = risk;
                            continue;
                        }


                        if (hillshade > 2 * ambient) {
                            if(weather.getCloudSum() == null || weather.getCloudSum() < 6)
                                risk += 0.1;
                        }
                        if (weather.getWindAvg() != null && weather.getWindAvg() > 30)
                            risk += 0.1;
                        if (weather.getWindAvg() != null && weather.getWindAvg() > 80)
                            risk += 0.2;
                        if (weather.getSnowLevel() != null && weather.getSnowLevel() > 100)
                            risk += 0.2;

                        if (t[TerrainProps.GRADE] < Math.toRadians(25) || t[TerrainProps.GRADE] > Math.toRadians(60))
                            risk = 0;
                        if (weather.getSnowLevel() != null && weather.getSnowLevel() < 20)
                            risk = 0;
                        r[RiskProps.RISK] = risk;
                    }
                }

                return results;
            }
        };
    }
}
