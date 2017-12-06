package backend.rasterizer.tasks;

import backend.rasterizer.GridTin;
import javafx.concurrent.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class TinTerrain extends Task<float[][][]> {

    private Future<GridTin> fgt;
    private ExecutorService executor;
    private int subtaskCount;

    public TinTerrain(ExecutorService executor, int subtaskCount, Future<GridTin> fgt) {
        this.executor = executor;
        this.fgt = fgt;
        this.subtaskCount = subtaskCount;
    }

    public float[][][] call() throws Exception {
        return new float[][][]{};
    }
}
