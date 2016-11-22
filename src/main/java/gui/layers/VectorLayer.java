package gui.layers;

import gui.Layer;
import gui.Viewport;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// TODO Add some data source / model to layer

public class VectorLayer extends Layer {

    public VectorLayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public void render(GraphicsContext gc, Viewport vp) {

    }
}
