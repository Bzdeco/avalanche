package gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Layer {
    private final BooleanProperty visible = new SimpleBooleanProperty(false);

    private final String name;
    private final Color color;

    protected Color getColor() { return color; }
    protected String getName() { return name; }

    public Layer(String name, Color color) {
        this.name  = name;
        this.color = color;
    }

    public final boolean isVisible() {
        return visible.get();
    }
    public final void setVisible(boolean visible) { this.visible.set(visible); }
    public final BooleanProperty isVisibleProperty() { return visible; }

    public abstract void render(GraphicsContext gc, double width, double height);
}
