package backend.rasterizer;

import tinfour.gwr.BandwidthSelectionMethod;
import tinfour.gwr.SurfaceModel;
import tinfour.interpolation.GwrTinInterpolator;
import tinfour.testutils.GridSpecification;
import tinfour.virtual.VirtualIncrementalTin;

public class NormalVector {
    private VirtualIncrementalTin tin;
    private GwrTinInterpolator interpolator;
    private GridSpecification grid;
    private double[][][] normalVectors;

    public double[][][] getNormalVectors() {
        if(normalVectors == null) {
            this.interpolator = new GwrTinInterpolator(tin);
            countNormalVectors();
        }
        return normalVectors;
    }

    public NormalVector(VirtualIncrementalTin tin, GridSpecification grid) {
        this.tin = tin;
        this.grid = grid;
        this.normalVectors = null;
        this.interpolator = null;
    }

    private void countNormalVectors() {
        int nRows = grid.getRowCount();
        int nCols = grid.getColumnCount();
        double[][][] result = new double[nRows][nCols][3];
        for (int xCol = 0; xCol < nRows; xCol++) {
            for (int yRow = 0; yRow < nCols; yRow++) {
                double z = interpolator.interpolate(SurfaceModel.CubicWithCrossTerms,
                        BandwidthSelectionMethod.FixedProportionalBandwidth, 1.0,
                        xCol, yRow, null);
                if (Double.isNaN(z)) {
                    result[xCol][yRow][0] = -1;
                } else {
                    double[] n = interpolator.getSurfaceNormal();
                    result[xCol][yRow][0] = n[0];
                    result[xCol][yRow][1] = n[1];
                    result[xCol][yRow][2] = n[2];
                }
            }
        }
        this.normalVectors = result;
    }
}
