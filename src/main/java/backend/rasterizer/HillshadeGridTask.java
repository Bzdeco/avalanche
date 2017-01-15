package backend.rasterizer;

import javafx.concurrent.Task;
import tinfour.testutils.GridSpecification;
import tinfour.semivirtual.SemiVirtualIncrementalTin;

public class HillshadeGridTask extends Task<float[][]> {
    private SemiVirtualIncrementalTin tin;
    private GridSpecification grid;
    private float[][][] normVectors;

    private double sunAzimuth;
    private double sunElevation;

    private float ambient;

    public HillshadeGridTask(float[][][] normalVector, float ambient) {
        this.normVectors = normalVector;

        // TODO calc from center of grid
        this.sunAzimuth = Math.toRadians(135);
        this.sunElevation = Math.toRadians(45);

        this.ambient = ambient;
    }

    @Override
    public float[][] call() {

        float directLight = 1f - ambient;

        // create a unit vector pointing at illumination source
        double cosA = Math.cos(sunAzimuth);
        double sinA = Math.sin(sunAzimuth);
        double cosE = Math.cos(sunElevation);
        double sinE = Math.sin(sunElevation);
        double xSun = cosA * cosE;
        double ySun = sinA * cosE;
        double zSun = sinE;

        return Utils.gmap2f(normVectors, n -> {
            if(n[0] == -1) return 0f; //not a number
            // n[0], n[1], n[2]  give x, y, and z values
            float cosTheta = (float)Math.max(0, n[0] * xSun + n[1] * ySun + n[2] * zSun);
            return Utils.clamp(0, cosTheta * directLight + ambient, 1);
        });
    }
}
