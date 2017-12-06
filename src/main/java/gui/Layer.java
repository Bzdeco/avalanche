package gui;

import javafx.beans.property.*;
import javafx.scene.canvas.GraphicsContext;

public abstract class Layer {
    protected final ReadOnlyBooleanWrapper isReady = new ReadOnlyBooleanWrapper();
    private final BooleanProperty visible = new SimpleBooleanProperty(false);
    private final StringProperty name = new SimpleStringProperty();

    public Layer(String name) {
        this.name.set(name);
    }

    protected String getName() {
        return name.get();
    }

    protected StringProperty nameProperty() {
        return name;
    }

    public final boolean isVisible() {
        return visible.get();
    }

    public final BooleanProperty isVisibleProperty() {
        return visible;
    }

    public final ReadOnlyBooleanProperty isReadyProperty() {
        return isReady.getReadOnlyProperty();
    }

    public final Boolean isReady() {
        return isReady.get();
    }

    public abstract void render(GraphicsContext gc, Viewport vp);
}
