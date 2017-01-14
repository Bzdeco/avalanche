package backend.rasterizer;

import javafx.concurrent.Task;
import tinfour.interpolation.IInterpolatorOverTin;
import tinfour.testutils.GridSpecification;
import tinfour.testutils.InterpolationMethod;
import tinfour.semivirtual.SemiVirtualIncrementalTin;

public class TerrainGridTask extends ChainTask<float[][]> {
    private SemiVirtualIncrementalTin tin;
    private GridSpecification grid;

    public TerrainGridTask(SemiVirtualIncrementalTin tin, GridSpecification grid) {
        this.tin = tin;
        this.grid = grid;
    }

    @Override
    public float[][] call() {
        IInterpolatorOverTin interpolator = InterpolationMethod.NaturalNeighbor.getInterpolator(tin);
        return Utils.renderGrid(grid, (xCol, yRow) -> interpolator.interpolate(xCol, yRow, null));
    }
}
