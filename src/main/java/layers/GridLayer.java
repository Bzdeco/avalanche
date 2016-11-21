package layers;

import com.sun.javafx.util.Utils;
import gui.Layer;
import gui.Viewport;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import org.reactfx.EventStreams;

// TODO Add some data source / model to layer

public class GridLayer extends Layer {

    public GridLayer(String name, Color color) {
        super(name, color);
        EventStreams.changesOf(dataProperty()).map(c -> c.getNewValue() != null).feedTo(isReady);
    }

    @Override
    public void render(GraphicsContext gc, Viewport vp) {
        double [][]arr = getData();

        Color layerColor = getColor();
        int layerAlpha = (int)(layerColor.getOpacity() * 0xFF);
        int layerArgb = (0xFF << 24)
                      | ((int)(layerColor.getRed() * 255) << 16)
                      | ((int)(layerColor.getGreen() * 255) << 8)
                      | ((int)(layerColor.getBlue() * 255));

        double cellSize = Math.max(1, Math.floor(16 * vp.getZoom()));

        int arrHeight = arr.length;
        int arrWidth = arr[0].length;

        int cellsX = Utils.clamp(0, (int)Math.ceil(vp.getWidth() / cellSize), arrWidth);
        int cellsY = Utils.clamp(0, (int)Math.ceil(vp.getHeight() / cellSize), arrHeight);

        int offX = (arrWidth - cellsX) / 2 + (int)Math.ceil(vp.getPan().getX() / cellSize);
        int offY = (arrHeight - cellsY) / 2 + (int)Math.ceil(vp.getPan().getY() / cellSize);

        PixelWriter pw = gc.getPixelWriter();
        final int cs = (int)cellSize;

        for(int y = 0; y < cellsY; ++y) {
            final int idxY = y + offY, sY = y * cs;
            for (int dy = 0; dy < cs; ++dy) {
                for (int x = 0; x < cellsX; ++x) {
                    final int idxX = x + offX, sX = x * cs;
                    for (int dx = 0; dx < cs; ++dx) {
                        final int alpha = 0x00FFFFFF | ((idxY >= 0 && idxY < arrHeight && idxX >= 0 && idxX < arrWidth)
                                                            ? (int)(layerAlpha * arr[idxX][idxY]) << 24 : 0);
                        pw.setArgb(sX + dx, sY + dy, layerArgb & alpha);
                    }
                }
            }
        }
    }

}
