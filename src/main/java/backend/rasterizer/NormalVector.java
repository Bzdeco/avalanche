package backend.rasterizer;

import backend.serializers.DataSerializer;
import backend.resourceHandler;
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
    private DataSerializer<double[][][]> dataSerializer;

    public NormalVector(VirtualIncrementalTin tin, GridSpecification grid) {
        this.tin = tin;
        this.grid = grid;
        this.normalVectors = null;
        this.interpolator = null;
        this.dataSerializer = new DataSerializer<>(resourceHandler.getNormalVectorsSerialized());
    }

    public double[][][] getNormalVectors() {
        if (normalVectors == null) {
            this.calculateOrDeserializeNormalVectors();
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
    }

    private void calculateOrDeserializeNormalVectors() {
        if (this.dataSerializer.isSerializedDataExistence()) {
            this.normalVectors = this.dataSerializer.deserialize();
        } else {
            this.interpolator = new GwrTinInterpolator(this.tin);
            this.countNormalVectors();
            this.dataSerializer.serialize(this.normalVectors);
        }
    }
}
