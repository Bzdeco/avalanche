package backend.rasterizer.tasks;

import backend.serializers.DataSerializer;
import javafx.concurrent.Task;

import java.io.File;

public class SerTerrain extends Task<float[][][]> {
    private DataSerializer<float[][][]> ser;

    public SerTerrain(File serfile) {
        ser = new DataSerializer<>(serfile);
    }

    @Override
    protected float[][][] call() throws Exception {
        return ser.deserialize();
    }
}
