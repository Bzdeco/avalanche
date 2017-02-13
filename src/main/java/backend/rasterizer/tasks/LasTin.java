package backend.rasterizer.tasks;

import backend.rasterizer.GridTin;
import javafx.concurrent.Task;
import tinfour.common.IMonitorWithCancellation;
import tinfour.common.Vertex;
import tinfour.semivirtual.SemiVirtualIncrementalTin;
import tinfour.testutils.GridSpecification;
import tinfour.testutils.VertexLoader;

import java.io.File;
import java.util.List;

public class LasTin extends Task<GridTin> {
    private File lasfile;

    private VertexLoader loader = new VertexLoader();

    private Boolean verticesLoaded = false;

    private IMonitorWithCancellation mon = new IMonitorWithCancellation() {
        @Override
        public int getReportingIntervalInPercent() {
            return 5;
        }

        @Override
        public void reportProgress(int pval) {
            updateProgress(verticesLoaded ? 50 + pval / 2 : pval / 2, 100);
        }

        @Override
        public void reportDone() {
        }

        @Override
        public void postMessage(String message) {
            updateMessage(message);
        }

        @Override
        public boolean isCanceled() {
            return LasTin.this.isCancelled();
        }
    };

    public LasTin(File lasfile) {
        this.lasfile = lasfile;
        loader.setPreSortEnabed(true);
    }

    @Override
    public GridTin call() throws Exception {
        List<Vertex> vertexList = loader.readLasFile(lasfile, null, mon);

        verticesLoaded = true;

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

        GridSpecification grid = new GridSpecification(GridSpecification.CellPosition.CenterOfCell,
                cellSize, xmin, xmax, ymin, ymax,
                geoScaleX, geoScaleY, geoOffsetX, geoOffsetY);

        SemiVirtualIncrementalTin tin = new SemiVirtualIncrementalTin(cellSize);
        tin.add(vertexList, mon);

        return new GridTin(tin, grid);
    }
}
