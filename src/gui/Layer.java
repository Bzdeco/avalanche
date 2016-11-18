package gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Layer {
    private boolean visible = false;
    private String name;
    private Color color;

    public Color getColor() { return color; }
    public String getName() { return name; }

    public Layer(String name, Color color) {
        this.name  = name;
        this.color = color;
    }

    public final boolean isVisible() {
        return visible;
    }

    public final void setVisible(boolean visible) {
        this.visible = visible;
    }

    public abstract void render(GraphicsContext gc, double width, double height);
}
