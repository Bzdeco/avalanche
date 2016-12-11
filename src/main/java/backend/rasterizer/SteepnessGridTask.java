package backend.rasterizer;

import javafx.concurrent.Task;
import tinfour.gwr.BandwidthSelectionMethod;
import tinfour.gwr.SurfaceModel;
import tinfour.interpolation.GwrTinInterpolator;
import tinfour.testutils.GridSpecification;
import tinfour.virtual.VirtualIncrementalTin;


public class SteepnessGridTask extends Task<float[][]> {
    private VirtualIncrementalTin tin;
    private GridSpecification grid;

    public SteepnessGridTask(VirtualIncrementalTin tin, GridSpecification grid) {
        this.tin = tin;
        this.grid = grid;
    }

    @Override
    protected float[][] call() {
        GwrTinInterpolator interpolator = new GwrTinInterpolator(tin);

        return Utils.renderGrid(grid, (xCol, yRow) -> {
            double z = interpolator.interpolate(SurfaceModel.CubicWithCrossTerms,
                    BandwidthSelectionMethod.FixedProportionalBandwidth, 1.0,
                    xCol, yRow, null);

            if(Double.isNaN(z)) return 0;

            double[] n = interpolator.getSurfaceNormal();

            // https://en.wikipedia.org/wiki/Spherical_coordinate_system#Cartesian_coordinates
            double r = Math.sqrt(n[0] * n[0] + n[1] * n[1] + n[2] * n[2]);
            return Math.acos(n[2] / r);
        });
    }
}
