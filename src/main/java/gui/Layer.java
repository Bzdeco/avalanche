package gui;

import javafx.beans.property.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Layer {
    private final BooleanProperty visible = new SimpleBooleanProperty(false);

    private final StringProperty name = new SimpleStringProperty();
    protected String getName() { return name.get(); }
    protected  StringProperty nameProperty() { return name; }

    public Layer(String name) {
        this.name.set(name);
    }

    public final boolean isVisible() {
        return visible.get();
    }
    public final void setVisible(boolean visible) { this.visible.set(visible); }
    public final BooleanProperty isVisibleProperty() { return visible; }

    protected final ReadOnlyBooleanWrapper isReady = new ReadOnlyBooleanWrapper();
    public final ReadOnlyBooleanProperty isReadyProperty() {
        return isReady.getReadOnlyProperty();
    }
    public final Boolean isReady() { return isReady.get(); }

    public abstract void render(GraphicsContext gc, Viewport vp);
}
