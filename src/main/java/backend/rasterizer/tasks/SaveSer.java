package backend.rasterizer.tasks;

import backend.serializers.DataSerializer;
import javafx.concurrent.Task;

import java.io.File;

public class SaveSer extends Task<Void> {
    private DataSerializer<float[][][]> ser;
    private float[][][] terrain;

    public SaveSer(File serfile, float[][][] terrain) {
        ser = new DataSerializer<>(serfile);
        this.terrain = terrain;
    }

    @Override
    protected Void call() throws Exception {
        ser.serialize(terrain);

        return null;
    }
}
