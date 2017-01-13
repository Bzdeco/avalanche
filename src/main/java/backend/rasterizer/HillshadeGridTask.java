package backend.rasterizer;

import tinfour.testutils.GridSpecification;
import tinfour.virtual.VirtualIncrementalTin;

public class HillshadeGridTask extends ChainTask<float[][]> {
    private VirtualIncrementalTin tin;
    private GridSpecification grid;
    private float[][][] normVectors;

    private double sunAzimuth;
    private double sunElevation;
    private double ambient;

    public HillshadeGridTask(VirtualIncrementalTin tin, GridSpecification grid, double ambient, float[][][] normalVector) {
        this.tin = tin;
        this.grid = grid;
        this.normVectors = normalVector;
        // TODO calc from center of grid
        this.sunAzimuth = Math.toRadians(135);
        this.sunElevation = Math.toRadians(45);

        this.ambient = ambient;
    }

    @Override
    public float[][] call() {

        double directLight = 1.0 - ambient;

        // create a unit vector pointing at illumination source
        double cosA = Math.cos(sunAzimuth);
        double sinA = Math.sin(sunAzimuth);
        double cosE = Math.cos(sunElevation);
        double sinE = Math.sin(sunElevation);
        double xSun = cosA * cosE;
        double ySun = sinA * cosE;
        double zSun = sinE;

        return Utils.renderGridNorm(grid, (iCol, iRow) -> {
            float[] n = normVectors[(int)iRow][(int)iCol];
            if(n[0] == -1) //not a number
                return 0;
            // n[0], n[1], n[2]  give x, y, and z values
            double cosTheta = Math.max(0, n[0] * xSun + n[1] * ySun + n[2] * zSun);
            return Utils.clamp(0, cosTheta * directLight + ambient, 1);
        });
    }
}
