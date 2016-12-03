package backend.rasterizer;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import com.sun.javafx.util.Utils;
import tinfour.common.IIncrementalTin;
import tinfour.common.Vertex;

import tinfour.gwr.BandwidthSelectionMethod;
import tinfour.gwr.SurfaceModel;
import tinfour.interpolation.GwrTinInterpolator;
import tinfour.interpolation.IInterpolatorOverTin;
import tinfour.utils.TinInstantiationUtility;

import tinfour.testutils.GridSpecification;
import tinfour.testutils.InterpolationMethod;
import tinfour.testutils.VertexLoader;


public class LasRasterizer {
    private GridSpecification grid;
    private IIncrementalTin tin;

    public LasRasterizer(File lasfile) throws IOException {
        VertexLoader loader = new VertexLoader();
        List<Vertex> vertexList = loader.readLasFile(lasfile, null, null);

        int nVertices = vertexList.size();
        double xmin = loader.getXMin();
        double xmax = loader.getXMax();
        double ymin = loader.getYMin();
        double ymax = loader.getYMax();

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

        grid = new GridSpecification(GridSpecification.CellPosition.CenterOfCell, cellSize, xmin, xmax, ymin, ymax,
                                     geoScaleX, geoScaleY, geoOffsetX, geoOffsetY);

        TinInstantiationUtility tiu = new TinInstantiationUtility(0.5, nVertices);
        Class<?> tinClass = tiu.getTinClass();

        tin = tiu.constructInstance(tinClass, cellSize);

        tin.add(vertexList, null);
    }

    public double[][] getTerrainGrid() {
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

    public double[][] getHillshadeGrid() {
        int nRows = grid.getRowCount();
        int nCols = grid.getColumnCount();
        double xLL = grid.getLowerLeftX();
        double yUL = grid.getUpperRightY();
        double cellSize = grid.getCellSize();

        double ambient = 0.25;
        double directLight = 1.0 - ambient;
        double sunAzimuth = Math.toRadians(135);
        double sunElevation = Math.toRadians(45);

        // create a unit vector pointing at illumination source
        double cosA = Math.cos(sunAzimuth);
        double sinA = Math.sin(sunAzimuth);
        double cosE = Math.cos(sunElevation);
        double sinE = Math.sin(sunElevation);
        double xSun = cosA * cosE;
        double ySun = sinA * cosE;
        double zSun = sinE;

        GwrTinInterpolator interpolator = new GwrTinInterpolator(tin);
        double results[][] = new double[nRows][nCols];

        for (int iRow = 0; iRow < nRows; iRow++) {
            double[] row = results[iRow];
            double yRow = yUL - iRow * cellSize;
            for (int iCol = 0; iCol < nCols; iCol++) {
                double xCol = iCol * cellSize + xLL;
                double z = interpolator.interpolate(SurfaceModel.CubicWithCrossTerms,
                        BandwidthSelectionMethod.FixedProportionalBandwidth, 1.0,
                        xCol, yRow, null);
                if (Double.isNaN(z)) {
                    row[iCol] = 0;
                } else {
                    double[] n = interpolator.getSurfaceNormal();
                    // n[0], n[1], n[2]  give x, y, and z values
                    double cosTheta = Math.max(0, n[0] * xSun + n[1] * ySun + n[2] * zSun);
                    row[iCol] = Utils.clamp(0, cosTheta * directLight + ambient, 1);
                }
            }
        }

        return results;
    }
}

