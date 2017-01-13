package backend.rasterizer;

import tinfour.testutils.GridSpecification;

import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;

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

    public static <U> float[][] gmap2f(U[][] arr, Function<U, Float> f) {
        int nRows = arr.length;
        int nCols = arr[0].length;

        float results[][] = new float[nRows][nCols];

        for (int iRow = 0; iRow < nRows; iRow++) {
            for (int iCol = 0; iCol < nCols; iCol++) {
                results[iRow][iCol] = f.apply(arr[iRow][iCol]);
            }
        }

        return results;
    }
}