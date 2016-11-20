package layers;

import com.sun.javafx.util.Utils;
import gui.Layer;
import gui.Viewport;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// TODO Add some data source / model to layer

public class GridLayer extends Layer {

    public GridLayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public void render(GraphicsContext gc, Viewport vp) {
        double [][]arr = getData();
        if(arr == null) return;

        Color basic = getColor();

        double cellSize = 16 * vp.getZoom();

        int arrHeight = arr.length;
        int arrWidth = arr[0].length;

        int cellsX = Utils.clamp(0, (int)Math.ceil(vp.getWidth() / cellSize), arrWidth);
        int cellsY = Utils.clamp(0, (int)Math.ceil(vp.getHeight() / cellSize), arrHeight);

        int offX = (arrWidth - cellsX) / 2 + (int)Math.ceil(vp.getPan().getX() / cellSize);
        int offY = (arrHeight - cellsY) / 2 + (int)Math.ceil(vp.getPan().getY() / cellSize);

        for (int y = 0; y < cellsY; ++y) {
            for (int x = 0; x < cellsX; ++x) {
                final int idxY = y + offY, idxX = x + offX;
                if(idxY >= 0 && idxY < arrHeight && idxX >= 0 && idxX < arrWidth)
                    gc.setFill(basic.deriveColor(0, 1, arr[idxY][idxX], 1));
                else
                    gc.setFill(basic.deriveColor(0, 0, 0, 1));

                gc.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }
    }

}
