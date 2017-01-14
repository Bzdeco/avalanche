package gui.layers;

import gui.Layer;
import gui.Viewport;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import org.reactfx.EventStreams;

import java.util.function.Function;

public class MultiGridLayer extends Layer {
    private ObjectProperty<float[][][]> data = new SimpleObjectProperty<>();
    public float[][][] getData() { return data.get(); }
    public ObjectProperty<float[][][]> dataProperty() { return data; }
    public void setData(float[][][] data) { this.data.set(data); }

    private int idx;
    private Function<Float, Integer> colorMapper;

    public MultiGridLayer(String name, int idx, Function<Float, Integer> colorMapper) {
        super(name);
        this.colorMapper = colorMapper;
        this.idx = idx;
        EventStreams.changesOf(dataProperty()).map(c -> c.getNewValue() != null).feedTo(isReady);
    }

    @Override
    public void render(GraphicsContext gc, Viewport vp) {
        float [][][]arr = getData();

        double cellSize = Math.max(1, Math.floor(16 * vp.getZoom()));

        int arrHeight = arr.length;
        int arrWidth = arr[0].length;

        int cellsX = Math.max(0, (int)Math.ceil(vp.getWidth() / cellSize));
        int cellsY = Math.max(0, (int)Math.ceil(vp.getHeight() / cellSize));

        int offX = (arrWidth - cellsX) / 2 + (int)Math.ceil(vp.getPan().getX() / cellSize);
        int offY = (arrHeight - cellsY) / 2 + (int)Math.ceil(vp.getPan().getY() / cellSize);

        PixelWriter pw = gc.getPixelWriter();
        final int cs = (int)cellSize;

        for (int y = 0; y < cellsY; ++y) {
            final int idxY = y + offY, sY = y * cs;
            if (idxY >= 0 && idxY < arrHeight) {
                final float[][] row = arr[idxY];
                for (int dy = 0; dy < cs; ++dy) {
                    for (int x = 0; x < cellsX; ++x) {
                        final int idxX = x + offX, sX = x * cs;
                        if(idxX >= 0 && idxX < arrWidth) {
                            for (int dx = 0; dx < cs; ++dx) {
                                pw.setArgb(sX + dx, sY + dy, colorMapper.apply(row[idxX][idx]));
                            }
                        }
                    }
                }
            }
        }
    }

}
