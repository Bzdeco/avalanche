package gui.layers;

import gui.Layer;
import gui.Viewport;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BackgroundLayer extends Layer {
    public BackgroundLayer(String name) {
        super(name);
        isReady.set(true);
    }

    @Override
    public void render(GraphicsContext gc, Viewport vp) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, vp.getWidth(), vp.getHeight());
    }
}
