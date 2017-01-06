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
    private double[][][] normVectors;

    public SteepnessGridTask(VirtualIncrementalTin tin, GridSpecification grid, double[][][] normalVector) {
        this.tin = tin;
        this.grid = grid;
        this.normVectors = normalVector;
    }

    @Override
    protected float[][] call() {
        float[][] result = Utils.renderGridNorm(grid, (iCol, iRow) -> {
            double[] n = normVectors[(int)iRow][(int)iCol];
            if(n[0] == -1) //not a number
                return 0;
            // https://en.wikipedia.org/wiki/Spherical_coordinate_system#Cartesian_coordinates
            double r = Math.sqrt(n[0] * n[0] + n[1] * n[1] + n[2] * n[2]);
            return Math.acos(n[2] / r);
        });
        return result;
    }
}
