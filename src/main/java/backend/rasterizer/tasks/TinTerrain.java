package backend.rasterizer.tasks;

import backend.rasterizer.GridTin;
import backend.rasterizer.TerrainProps;
import javafx.concurrent.Task;
import tinfour.gwr.BandwidthSelectionMethod;
import tinfour.gwr.SurfaceModel;
import tinfour.interpolation.GwrTinInterpolator;
import tinfour.interpolation.IInterpolatorOverTin;
import tinfour.testutils.GridSpecification;
import tinfour.semivirtual.SemiVirtualIncrementalTin;
import tinfour.testutils.InterpolationMethod;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TinTerrain extends Task<float[][][]> {

    private Future<GridTin> fgt;

    public TinTerrain(Future<GridTin> fgt) {
        this.fgt = fgt;
    }

    public float[][][] call() throws ExecutionException, InterruptedException {
        GridTin gt = fgt.get();
        SemiVirtualIncrementalTin tin = gt.getTin();
        GridSpecification grid = gt.getGrid();

        GwrTinInterpolator gwr = new GwrTinInterpolator(tin);
        IInterpolatorOverTin nn = InterpolationMethod.NaturalNeighbor.getInterpolator(tin);

        int nRows = grid.getRowCount();
        int nCols = grid.getColumnCount();
        double xLL = grid.getLowerLeftX();
        double yUL = grid.getUpperRightY();
        double cellSize = grid.getCellSize();

        float[][][] result = new float[nRows][nCols][8];

        for (int iRow = 0; iRow < nRows; iRow++) {
            final double yRow = yUL - iRow * cellSize;
            for (int iCol = 0; iCol < nCols; iCol++) {
                final double xCol = iCol * cellSize + xLL;
                final double z = gwr.interpolate(SurfaceModel.CubicWithCrossTerms,
                        BandwidthSelectionMethod.FixedProportionalBandwidth, 100,
                        xCol, yRow, null);
                if (!Double.isNaN(z)) {
                    final double[] n = gwr.getSurfaceNormal();
                    final double[] beta = gwr.getCoefficients();

                    final float zX = (float)beta[1], zY = (float)beta[2],
                               zXX = 2*(float)beta[3], zYY = 2*(float)beta[4], zXY = (float)beta[4];

                    float kP = (zXX*zX*zX + 2*zXY*zX*zY + zYY*zY*zY) / ((zX*zX+zY*zY)*(float)Math.pow(zX*zX+zY*zY+1.0, 1.5));
                    float kPl = (zXX*zX*zX - 2*zXY*zX*zY + zYY*zY*zY) / (float)Math.pow(zX*zX+zY*zY, 1.5);

                    float[] r = result[iRow][iCol];

                    r[TerrainProps.ALTITUDE]  = (float)nn.interpolate(xCol, yRow, null);
                    r[TerrainProps.NORMALX]   = (float)n[0];
                    r[TerrainProps.NORMALY]   = (float)n[1];
                    r[TerrainProps.NORMALZ]   = (float)n[2];
                    r[TerrainProps.ASPECT]    = (float)Math.atan2(zY, zX);
                    r[TerrainProps.GRADE]     = (float)Math.atan(Math.sqrt(zX*zX+zY*zY));
                    r[TerrainProps.PROFCURV]  = kP;
                    r[TerrainProps.PLANCURV]  = kPl;
                }
            }
        }

        return result;
    }
}
