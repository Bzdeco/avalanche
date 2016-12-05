package backend.rasterizer;

import javafx.concurrent.Task;
import tinfour.interpolation.IInterpolatorOverTin;
import tinfour.testutils.GridSpecification;
import tinfour.testutils.InterpolationMethod;
import tinfour.virtual.VirtualIncrementalTin;

public class TerrainGridTask extends Task<double[][]> {
    private VirtualIncrementalTin tin;
    private GridSpecification grid;

    public TerrainGridTask(VirtualIncrementalTin tin, GridSpecification grid) {
        this.tin = tin;
        this.grid = grid;
    }

    @Override
    protected double[][] call() {
        int nRows = grid.getRowCount();
        int nCols = grid.getColumnCount();
        double xLL = grid.getLowerLeftX();
        double yUL = grid.getUpperRightY();
        double cellSize = grid.getCellSize();

        IInterpolatorOverTin interpolator = InterpolationMethod.NaturalNeighbor.getInterpolator(tin);

        double results[][] = new double[nRows][nCols];

        double eMin = Double.POSITIVE_INFINITY;
        double eMax = Double.NEGATIVE_INFINITY;

        for (int iRow = 0; iRow < nRows; iRow++) {
            final double[] row = results[iRow];
            final double yRow = yUL - iRow * cellSize;
            for (int iCol = 0; iCol < nCols; iCol++) {
                final double xCol = iCol * cellSize + xLL;
                final double val = interpolator.interpolate(xCol, yRow, null);
                row[iCol] = val;
                if(!Double.isNaN(val)) {
                    eMin = Math.min(eMin, val);
                    eMax = Math.max(eMax, val);
                }
            }
        }

        for (int iRow = 0; iRow < nRows; iRow++) {
            for (int iCol = 0; iCol < nCols; iCol++) {
                double val = (results[iRow][iCol] - eMin) / (eMax - eMin);
                results[iRow][iCol] = Double.isNaN(val) ? 0 : val;
            }
        }

        return results;
    }
}
