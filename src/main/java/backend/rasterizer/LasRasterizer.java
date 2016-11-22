package backend.rasterizer;

import tinfour.las.LasFileReader;
import tinfour.las.LasPoint;

import java.io.File;
import java.io.IOException;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import tinfour.common.IIncrementalTin;
import tinfour.common.Vertex;

import tinfour.interpolation.IInterpolatorOverTin;
import tinfour.utils.TinInstantiationUtility;

import tinfour.testutils.GridSpecification;
import tinfour.testutils.InterpolationMethod;
import tinfour.testutils.VertexLoader;


public class LasRasterizer {
    public static double[][] rasterize(File lasfile) throws IOException {
        VertexLoader loader = new VertexLoader();
        List<Vertex> vertexList = loader.readLasFile(lasfile, null, null);

        int nVertices = vertexList.size();
        double xmin = loader.getXMin();
        double xmax = loader.getXMax();
        double ymin = loader.getYMin();
        double ymax = loader.getYMax();
        double zmin = loader.getZMin();
        double zmax = loader.getZMax();

        double area = (xmax - xmin) * (ymax - ymin);
        double cellSize = 0.87738 * Math.sqrt(area / nVertices);

        double geoScaleX = 0;
        double geoScaleY = 0;
        double geoOffsetX = 0;
        double geoOffsetY = 0;
        if (loader.isSourceInGeographicCoordinates()) {
            geoScaleX = loader.getGeoScaleX();
            geoScaleY = loader.getGeoScaleY();
            geoOffsetX = loader.getGeoOffsetX();
            geoOffsetY = loader.getGeoOffsetY();
        }

        GridSpecification grid = new GridSpecification(GridSpecification.CellPosition.CenterOfCell, cellSize,
                                                        xmin, xmax, ymin, ymax,
                                                        geoScaleX, geoScaleY, geoOffsetX, geoOffsetY);

        TinInstantiationUtility tiu = new TinInstantiationUtility(0.5, nVertices);
        Class<?> tinClass = tiu.getTinClass();
        IIncrementalTin tin = tiu.constructInstance(tinClass, cellSize);

        tin.add(vertexList, null);

        int nRows = grid.getRowCount();
        int nCols = grid.getColumnCount();
        double xLL = grid.getLowerLeftX();
        double yUL = grid.getUpperRightY();

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

