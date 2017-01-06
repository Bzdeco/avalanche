package backend.rasterizer;

import tinfour.gwr.BandwidthSelectionMethod;
import tinfour.gwr.SurfaceModel;
import tinfour.interpolation.GwrTinInterpolator;
import tinfour.testutils.GridSpecification;
import tinfour.virtual.VirtualIncrementalTin;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class NormalVector {
    private VirtualIncrementalTin tin;
    private GwrTinInterpolator interpolator;
    private GridSpecification grid;
    private double[][][] normalVectors;

    public NormalVector(VirtualIncrementalTin tin, GridSpecification grid) {
        this.tin = tin;
        this.grid = grid;
        this.normalVectors = null;
        this.interpolator = null;
    }

    public double[][][] getNormalVectors() {
        if (normalVectors == null) {
            this.interpolator = new GwrTinInterpolator(tin);
            countNormalVectors();
        }
        return normalVectors;
    }

    private void countNormalVectors() {
        int nRows = grid.getRowCount();
        int nCols = grid.getColumnCount();
        double xLL = grid.getLowerLeftX();
        double yUL = grid.getUpperRightY();
        double cellSize = grid.getCellSize();

        double[][][] result = new double[nRows][nCols][3];

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
                    result[iRow][iCol][0] = n[0];
                    result[iRow][iCol][1] = n[1];
                    result[iRow][iCol][2] = n[2];
                }
            }
        }
        this.normalVectors = result;
//        try {
//            FileOutputStream fileOut = new FileOutputStream("src/main/resources/norm.ser");
//            ObjectOutputStream out = new ObjectOutputStream(fileOut);
//            out.writeObject(normalVectors);
//            out.close();
//            fileOut.close();
//            System.out.printf("Serialized data is saved in src/main/resources/norm.ser");
//        } catch (IOException i) {
//            i.printStackTrace();
//        }
    }
}
