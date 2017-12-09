package old.legacy.tasks;

import avalanche.view.layers.magicalindexes.TerrainProps;
import com.sun.javafx.util.Utils;
import javafx.concurrent.Task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

//TODO this should be run at some point, but it does not
// if it runs it needs to be rebuilt
public class Hillshade extends Task<float[][]>
{
    private Future<float[][][]> fterrain;
    private double sunAzimuth;
    private double sunElevation;
    private float ambient;

    public Hillshade(Future<float[][][]> terrain, float ambient)
    {
        this.fterrain = terrain;

        // TODO calc from center of grid
        this.sunAzimuth = Math.toRadians(135);
        this.sunElevation = Math.toRadians(45);

        this.ambient = ambient;
    }

    @Override
    public float[][] call() throws ExecutionException, InterruptedException
    {
        float[][][] terrain = fterrain.get();

        float directLight = 1f - ambient;

        // create a unit vector pointing at illumination source
        double cosA = Math.cos(sunAzimuth);
        double sinA = Math.sin(sunAzimuth);
        double cosE = Math.cos(sunElevation);
        double sinE = Math.sin(sunElevation);
        double xSun = cosA * cosE;
        double ySun = sinA * cosE;
        double zSun = sinE;

        return gmap2f(terrain, n -> {
            float cosTheta = (float) Math.max(0, n[TerrainProps.NORMALX] * xSun
                    + n[TerrainProps.NORMALY] * ySun
                    + n[TerrainProps.NORMALZ] * zSun);
            return Utils.clamp(0, cosTheta * directLight + ambient, 1);
        });
    }

    public <U> float[][] gmap2f(U[][] arr, Function<U, Float> f) {
        int nRows = arr.length;
        int nCols = arr[0].length;

        float results[][] = new float[nRows][nCols];

        for (int iRow = 0; iRow < nRows; iRow++) {
            for (int iCol = 0; iCol < nCols; iCol++) {
                results[iRow][iCol] = f.apply(arr[iRow][iCol]);
            }
        }

        return results;
    }
}
