package gui;

import javafx.beans.property.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Layer {
    private final BooleanProperty visible = new SimpleBooleanProperty(false);

    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.BLACK);

    protected Color getColor() { return color.get(); }
    protected ObjectProperty<Color> colorProperty() { return color; }

    protected String getName() { return name.get(); }
    protected  StringProperty nameProperty() { return name; }

    public Layer(String name, Color color) {
        this.name.set(name);
        this.color.set(color);
    }

    public final boolean isVisible() {
        return visible.get();
    }
    public final void setVisible(boolean visible) { this.visible.set(visible); }
    public final BooleanProperty isVisibleProperty() { return visible; }

    public abstract void render(GraphicsContext gc, Viewport vp);


    public double[][] getData() { return data.get(); }
    public ObjectProperty<double[][]> dataProperty() { return data; }
    public void setData(double[][] data) { this.data.set(data); }

    private ObjectProperty<double[][]> data = new SimpleObjectProperty<>();
}
