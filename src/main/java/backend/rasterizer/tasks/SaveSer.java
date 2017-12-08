package backend.rasterizer.tasks;

import avalanche.model.LeData;
import avalanche.model.serializers.DataSerializer;
import javafx.concurrent.Task;

import java.io.File;

public class SaveSer extends Task<Void> {
    private DataSerializer<LeData> ser;
    private LeData terrain;

    public SaveSer(File serfile, LeData terrain) {
        ser = new DataSerializer<>(serfile);
        this.terrain = terrain;
    }

    @Override
    protected Void call() throws Exception {
        ser.serialize(terrain);
        return null;
    }
}
