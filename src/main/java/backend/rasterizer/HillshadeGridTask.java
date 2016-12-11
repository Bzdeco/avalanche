package backend.rasterizer;

import javafx.concurrent.Task;
import tinfour.gwr.BandwidthSelectionMethod;
import tinfour.gwr.SurfaceModel;
import tinfour.interpolation.GwrTinInterpolator;
import tinfour.testutils.GridSpecification;
import tinfour.virtual.VirtualIncrementalTin;

public class HillshadeGridTask extends Task<float[][]> {
    private VirtualIncrementalTin tin;
    private GridSpecification grid;

    private double sunAzimuth;
    private double sunElevation;
    private double ambient;

    public HillshadeGridTask(VirtualIncrementalTin tin, GridSpecification grid, double ambient) {
        this.tin = tin;
        this.grid = grid;

        // TODO calc from center of grid
        this.sunAzimuth = Math.toRadians(135);
        this.sunElevation = Math.toRadians(45);

        this.ambient = ambient;
    }

    @Override
    protected float[][] call() {
        int nRows = grid.getRowCount();
        int nCols = grid.getColumnCount();
        double xLL = grid.getLowerLeftX();
        double yUL = grid.getUpperRightY();
        double cellSize = grid.getCellSize();

        double directLight = 1.0 - ambient;

        // create a unit vector pointing at illumination source
        double cosA = Math.cos(sunAzimuth);
        double sinA = Math.sin(sunAzimuth);
        double cosE = Math.cos(sunElevation);
        double sinE = Math.sin(sunElevation);
        double xSun = cosA * cosE;
        double ySun = sinA * cosE;
        double zSun = sinE;

        GwrTinInterpolator interpolator = new GwrTinInterpolator(tin);

        return Utils.renderGrid(grid, (xCol, yRow) -> {
            double z = interpolator.interpolate(SurfaceModel.CubicWithCrossTerms,
                    BandwidthSelectionMethod.FixedProportionalBandwidth, 1.0,
                    xCol, yRow, null);
            if(Double.isNaN(z)) return 0;

            double[] n = interpolator.getSurfaceNormal();
            // n[0], n[1], n[2]  give x, y, and z values
            double cosTheta = Math.max(0, n[0] * xSun + n[1] * ySun + n[2] * zSun);
            return Utils.clamp(0, cosTheta * directLight + ambient, 1);
        });
    }
}
