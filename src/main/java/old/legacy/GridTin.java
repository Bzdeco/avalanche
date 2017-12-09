package old.legacy;

import tinfour.semivirtual.SemiVirtualIncrementalTin;
import tinfour.testutils.GridSpecification;

//TODO not removing this since @Bzdeco was mentioning this
//may be useful
public class GridTin {
    private SemiVirtualIncrementalTin tin;
    private GridSpecification grid;

    public GridTin(SemiVirtualIncrementalTin tin, GridSpecification grid) {
        this.tin = tin;
        this.grid = grid;
    }

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
}