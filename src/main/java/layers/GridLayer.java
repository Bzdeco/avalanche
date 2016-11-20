package layers;

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
        Color basic = getColor().deriveColor(0, 1, 1, .5);
        double cellSize = Math.min(vp.getWidth(), vp.getHeight()) / 100;

        for(int x = 0; x < 100; ++x) {
            for(int y = 0; y < 100; ++y) {
                gc.setFill(basic.deriveColor(0, Math.random(), 1, 1));
                gc.fillRect(x*cellSize,y*cellSize, cellSize, cellSize);
            }
        }
    }

}
