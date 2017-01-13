package backend.rasterizer;

import tinfour.gwr.BandwidthSelectionMethod;
import tinfour.gwr.SurfaceModel;
import tinfour.interpolation.GwrTinInterpolator;
import tinfour.testutils.GridSpecification;
import tinfour.virtual.VirtualIncrementalTin;

public class NormalsTask extends ChainTask<float[][][]>{
    private GridSpecification grid;
    private GwrTinInterpolator interpolator;

    public NormalsTask(VirtualIncrementalTin tin, GridSpecification grid) {
        this.grid = grid;
        this.interpolator = new GwrTinInterpolator(tin);
    }

    public float[][][] call() {
        int nRows = grid.getRowCount();
        int nCols = grid.getColumnCount();
        double xLL = grid.getLowerLeftX();
        double yUL = grid.getUpperRightY();
        double cellSize = grid.getCellSize();

        float[][][] result = new float[nRows][nCols][3];

        for (int iRow = 0; iRow < nRows; iRow++) {
            final double yRow = yUL - iRow * cellSize;
            for (int iCol = 0; iCol < nCols; iCol++) {
                final double xCol = iCol * cellSize + xLL;
                double z = interpolator.interpolate(SurfaceModel.CubicWithCrossTerms,
                        BandwidthSelectionMethod.FixedProportionalBandwidth, 1.0,
                        xCol, yRow, null);
                if (Double.isNaN(z)) {
                    result[iRow][iCol][0] = -1;
                } else {
                    double[] n = interpolator.getSurfaceNormal();
                    result[iRow][iCol][0] = (float)n[0];
                    result[iRow][iCol][1] = (float)n[1];
                    result[iRow][iCol][2] = (float)n[2];
                }
            }
        }

        return result;
    }
}
