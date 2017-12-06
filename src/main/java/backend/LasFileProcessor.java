package backend;

import backend.rasterizer.GridTin;
import backend.rasterizer.TerrainProps;
import javafx.concurrent.Task;
import tinfour.gwr.BandwidthSelectionMethod;
import tinfour.gwr.SurfaceModel;
import tinfour.interpolation.GwrTinInterpolator;
import tinfour.semivirtual.SemiVirtualIncrementalTin;
import tinfour.testutils.GridSpecification;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class LasFileProcessor implements FileProcessor
{
    private File file;
    private GridTin gridTin;
    private ExecutorService executor;
    private int subtaskCount;

    public LasFileProcessor(final File file)
    {
        this.file = file;
    }

    public LasFileProcessor(final File file, final GridTin gridTin, final ExecutorService executor, final int subtaskCount)
    {
        this.file = file;
        this.gridTin = gridTin;
        this.executor = executor;
        this.subtaskCount = subtaskCount;
    }

    @Override
    public Task<LeData> createProcessingTask()
    {
        GridSpecification grid = gridTin.getGrid();
        int nRows = grid.getRowCount();
        int nCols = grid.getColumnCount();

        float[][][] result = new float[nRows][nCols][8];

        int rowsPerSubtask = nRows / subtaskCount;

        List<Future<Void>> tasks = new LinkedList<>();

        for (int subtask_id = 0; subtask_id < subtaskCount; ++subtask_id) {
            final int i = subtask_id;

            tasks.add(executor.submit(() -> {
                int start = i * rowsPerSubtask, end = start + rowsPerSubtask;
                if (i == subtaskCount - 1) end = nRows;
                calculateParameters(gridTin, start, end, result);
                return null;
            }));
        }

        for (Future<Void> task : tasks) {
            try {
                task.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        return new LeData(result);
    }

    private void calculateParameters(GridTin gt, int startRow, int endRow, float[][][] result) {
        SemiVirtualIncrementalTin tin = gt.getTin();
        GridSpecification grid = gt.getGrid();

        int nCols = grid.getColumnCount();

        double xLL = grid.getLowerLeftX();
        double yUL = grid.getUpperRightY();
        double cellSize = grid.getCellSize();

        GwrTinInterpolator gwr = new GwrTinInterpolator(tin);

        for (int iRow = startRow; iRow < endRow; iRow++) {
            final double yRow = yUL - iRow * cellSize;
            for (int iCol = 0; iCol < nCols; iCol++) {
                final double xCol = iCol * cellSize + xLL;
                final double z = gwr.interpolate(SurfaceModel.CubicWithCrossTerms,
                        BandwidthSelectionMethod.FixedProportionalBandwidth, 100,
                        xCol, yRow, null);
                if (!Double.isNaN(z)) {
                    final double[] n = gwr.getSurfaceNormal();
                    final double[] beta = gwr.getCoefficients();

                    final float zX = (float) beta[1], zY = (float) beta[2],
                            zXX = 2 * (float) beta[3], zYY = 2 * (float) beta[4], zXY = (float) beta[4];

                    float kP = (zXX * zX * zX + 2 * zXY * zX * zY + zYY * zY * zY) / ((zX * zX + zY * zY) * (float) Math.pow(zX * zX + zY * zY + 1.0, 1.5));
                    float kPl = (zXX * zX * zX - 2 * zXY * zX * zY + zYY * zY * zY) / (float) Math.pow(zX * zX + zY * zY, 1.5);

                    float[] r = result[iRow][iCol];

                    r[TerrainProps.ALTITUDE] = (float) z;
                    r[TerrainProps.NORMALX] = (float) n[0];
                    r[TerrainProps.NORMALY] = (float) n[1];
                    r[TerrainProps.NORMALZ] = (float) n[2];
                    r[TerrainProps.ASPECT] = (float) Math.atan2(zY, zX);
                    r[TerrainProps.GRADE] = (float) Math.atan(Math.sqrt(zX * zX + zY * zY));
                    r[TerrainProps.PROFCURV] = kP;
                    r[TerrainProps.PLANCURV] = kPl;
                }
            }
        }
    }
}