package backend.rasterizer.tasks;

import backend.serializers.DataSerializer;
import backend.serializers.SerializerFactory;
import javafx.concurrent.Task;

import java.io.File;

public class SaveSer extends Task<Void> {
    private SerializerFactory serFactory;
    private DataSerializer<float[][][]> ser;
    private float[][][] terrain;

    public SaveSer(File serfile, float[][][] terrain) {
        serFactory = SerializerFactory.getInstance();
        ser = (DataSerializer<float[][][]>) serFactory.getSerializer("FLOAT[][][]", serfile);
        this.terrain = terrain;
    }

    @Override
    protected Void call() throws Exception {
        ser.serialize(terrain);
        return null;
    }
}
