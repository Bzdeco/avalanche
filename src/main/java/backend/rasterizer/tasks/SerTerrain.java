package backend.rasterizer.tasks;

import backend.serializers.DataSerializer;
import backend.serializers.SerializerFactory;
import javafx.concurrent.Task;

import java.io.File;

public class SerTerrain extends Task<float[][][]> {
    private SerializerFactory serFactory;
    private DataSerializer<float[][][]> ser;

    public SerTerrain(File serfile) {
        serFactory = SerializerFactory.getInstance();
        ser = (DataSerializer<float[][][]>) serFactory.getSerializer("FLOAT[][][]", serfile);
    }

    @Override
    protected float[][][] call() throws Exception {
        return ser.deserialize();
    }
}
