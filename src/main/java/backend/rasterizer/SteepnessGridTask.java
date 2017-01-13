package backend.rasterizer;

import javafx.concurrent.Task;
import tinfour.testutils.GridSpecification;
import tinfour.virtual.VirtualIncrementalTin;


public class SteepnessGridTask extends ChainTask<float[][]> {
    private VirtualIncrementalTin tin;
    private GridSpecification grid;
    private float[][][] normVectors;

    public SteepnessGridTask(VirtualIncrementalTin tin, GridSpecification grid, float[][][] normalVector) {
        this.tin = tin;
        this.grid = grid;
        this.normVectors = normalVector;
    }

    @Override
    public float[][] call() {
        float[][] result = Utils.renderGridNorm(grid, (iCol, iRow) -> {
            float[] n = normVectors[(int)iRow][(int)iCol];
            if(n[0] == -1) //not a number
                return 0;
            // https://en.wikipedia.org/wiki/Spherical_coordinate_system#Cartesian_coordinates
            double r = Math.sqrt(n[0] * n[0] + n[1] * n[1] + n[2] * n[2]);
            return Math.acos(n[2] / r);
        });
        return result;
    }
}
