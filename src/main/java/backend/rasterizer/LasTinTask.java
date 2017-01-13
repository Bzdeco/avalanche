package backend.rasterizer;

import javafx.concurrent.Task;
import tinfour.common.IMonitorWithCancellation;
import tinfour.common.Vertex;
import tinfour.testutils.GridSpecification;
import tinfour.testutils.VertexLoader;
import tinfour.virtual.VirtualIncrementalTin;

import java.io.File;
import java.util.List;

public class LasTinTask extends ChainTask<VirtualIncrementalTin> {
    private File lasfile;

    private VertexLoader loader = new VertexLoader();

    private GridSpecification grid;

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
        public void reportDone() {}

        @Override
        public void postMessage(String message) {
            updateMessage(message);
        }

        @Override
        public boolean isCanceled() { return LasTinTask.this.isCancelled(); }
    };;

    public LasTinTask(File lasfile) {
        this.lasfile = lasfile;
        loader.setPreSortEnabed(true);
    }

    public GridSpecification getGrid() {
        return grid;
    }

    @Override
    public VirtualIncrementalTin call() throws Exception {
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

        grid = new GridSpecification(GridSpecification.CellPosition.CenterOfCell, cellSize, xmin, xmax, ymin, ymax,
                geoScaleX, geoScaleY, geoOffsetX, geoOffsetY);

        VirtualIncrementalTin tin = new VirtualIncrementalTin(cellSize);
        tin.add(vertexList, mon);

        return tin;
    }
}
