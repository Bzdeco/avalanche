package backend.rasterizer;

import tinfour.testutils.GridSpecification;

import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;

public class Utils extends com.sun.javafx.util.Utils {
    public static float[][] renderGrid(GridSpecification grid, DoubleBinaryOperator inter) {
        int nRows = grid.getRowCount();
        int nCols = grid.getColumnCount();
        double xLL = grid.getLowerLeftX();
        double yUL = grid.getUpperRightY();
        double cellSize = grid.getCellSize();

        float results[][] = new float[nRows][nCols];

        for (int iRow = 0; iRow < nRows; iRow++) {
            final float[] row = results[iRow];
            final double yRow = yUL - iRow * cellSize;
            for (int iCol = 0; iCol < nCols; iCol++) {
                final double xCol = iCol * cellSize + xLL;
                row[iCol] = (float)inter.applyAsDouble(xCol, yRow);
            }
        }

        return results;
    }

    public static float[][] renderGridNorm(GridSpecification grid, DoubleBinaryOperator inter) {
        int nRows = grid.getRowCount();
        int nCols = grid.getColumnCount();

        float results[][] = new float[nRows][nCols];

        for (int iRow = 0; iRow < nRows; iRow++) {
            for (int iCol = 0; iCol < nCols; iCol++) {
                results[iRow][iCol] = (float)inter.applyAsDouble(iCol, iRow);
            }
        }

        return results;
    }
}