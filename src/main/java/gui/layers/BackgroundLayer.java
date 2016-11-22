package gui.layers;

import gui.Layer;
import gui.Viewport;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BackgroundLayer extends Layer {
    public BackgroundLayer(String name, Color color) {
        super(name, color);
        isReady.set(true);
    }

    @Override
    public void render(GraphicsContext gc, Viewport vp) {
        gc.setFill(getColor());
        gc.fillRect(0, 0, vp.getWidth(), vp.getHeight());
    }
}
