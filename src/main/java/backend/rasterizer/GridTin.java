package backend.rasterizer;

import tinfour.semivirtual.SemiVirtualIncrementalTin;
import tinfour.testutils.GridSpecification;

/**
 * Created by korri on 14/01/17.
 */
public class GridTin {
    private SemiVirtualIncrementalTin tin;
    private GridSpecification grid;

    public SemiVirtualIncrementalTin getTin() {
        return tin;
    }

    public void setTin(SemiVirtualIncrementalTin tin) {
        this.tin = tin;
    }

    public GridSpecification getGrid() {
        return grid;
    }

    public void setGrid(GridSpecification grid) {
        this.grid = grid;
    }

    public GridTin(SemiVirtualIncrementalTin tin, GridSpecification grid) {
        this.tin = tin;
        this.grid = grid;
    }
}
