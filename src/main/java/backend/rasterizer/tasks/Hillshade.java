package backend.rasterizer.tasks;

import backend.rasterizer.TerrainProps;
import backend.rasterizer.Utils;
import javafx.concurrent.Task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Hillshade extends Task<float[][]> {
    private Future<float[][][]> fterrain;

    private double sunAzimuth;
    private double sunElevation;

    private float ambient;

    public Hillshade(Future<float[][][]> terrain, float ambient) {
        this.fterrain = terrain;

        // TODO calc from center of grid
        this.sunAzimuth = Math.toRadians(135);
        this.sunElevation = Math.toRadians(45);

        this.ambient = ambient;
    }

    @Override
    public float[][] call() throws ExecutionException, InterruptedException {
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

        return Utils.gmap2f(terrain, n -> {
            float cosTheta = (float)Math.max(0, n[TerrainProps.NORMALX] * xSun
                                              + n[TerrainProps.NORMALY] * ySun
                                              + n[TerrainProps.NORMALZ] * zSun);
            return Utils.clamp(0, cosTheta * directLight + ambient, 1);
        });
    }
}
