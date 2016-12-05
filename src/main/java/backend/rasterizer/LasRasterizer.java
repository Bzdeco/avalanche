package backend.rasterizer;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import com.sun.javafx.util.Utils;
import javafx.scene.control.ProgressBar;

import org.reactfx.EventStreams;
import tinfour.gwr.BandwidthSelectionMethod;
import tinfour.gwr.SurfaceModel;
import tinfour.interpolation.GwrTinInterpolator;
import tinfour.interpolation.IInterpolatorOverTin;

import tinfour.testutils.GridSpecification;
import tinfour.testutils.InterpolationMethod;
import tinfour.testutils.VertexLoader;
import tinfour.virtual.VirtualIncrementalTin;


public class LasRasterizer {
    public LasRasterizer(File lasfile, ProgressBar p) {



        /*PrintStream ps = System.out;

        VertexLoader loader = new VertexLoader();

        loader.setPreSortEnabed(true);

        IMonitorWithCancellation mon = new IMonitorWithCancellation() {
            @Override
            public int getReportingIntervalInPercent() {
                return 5;
            }

            @Override
            public void reportProgress(int progressValueInPercent) {
                System.out.format("Progress: %d\n", progressValueInPercent);
            }

            @Override
            public void reportDone() {}

            @Override
            public void postMessage(String message) {
                System.out.println(message);
            }

            @Override
            public boolean isCanceled() { return false; }
        };

        long time0 = System.nanoTime();
        List<Vertex> vertexList = loader.readLasFile(lasfile, null, mon);
        long time1 = System.nanoTime();

        ps.format("Time to load LAS file (milliseconds):  %11.3f\n", (time1 - time0) / 1000000.0);

        int nVertices = vertexList.size();
        double xmin = loader.getXMin();
        double xmax = loader.getXMax();
        double ymin = loader.getYMin();
        double ymax = loader.getYMax();

        double area = (xmax - xmin) * (ymax - ymin);

        ps.format("Area: %11.3f\n", area);

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

        tin = new VirtualIncrementalTin(cellSize);

        time0 = System.nanoTime();
        tin.add(vertexList, mon);
        time1 = System.nanoTime();

        ps.format("Time to build TIN:  %11.3f\n", (time1 - time0) / 1000000.0);*/
    }

    /*public double[][] getHillshadeGrid(double sunAzimuth, double sunElevation) {
        int nRows = grid.getRowCount();
        int nCols = grid.getColumnCount();
        double xLL = grid.getLowerLeftX();
        double yUL = grid.getUpperRightY();
        double cellSize = grid.getCellSize();

        double ambient = 0.25;
        double directLight = 1.0 - ambient;

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
    }*/
}

