package avalanche.view.layers;

import avalanche.model.LeData;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.reactfx.EventStreams;

public abstract class LayerView implements Layer
{
    private ObjectProperty<LeData> data = new SimpleObjectProperty<>();
    private final ReadOnlyBooleanWrapper ready = new ReadOnlyBooleanWrapper();
    private final BooleanProperty visible = new SimpleBooleanProperty(false);
    private final StringProperty name = new SimpleStringProperty();

    public LayerView(String name) {
        this.name.set(name);
        EventStreams.changesOf(dataProperty())
                .map(c -> c.getNewValue() != null)
                .feedTo(ready);
    }

    public LeData getData() {
        return data.get();
    }

    public void setData(LeData data) {
        this.data.set(data);
    }

    public ObjectProperty<LeData> dataProperty() {
        return data;
    }

    protected String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public final boolean isVisible() {
        return visible.get();
    }

    public final BooleanProperty isVisibleProperty() {
        return visible;
    }

    public final ReadOnlyBooleanProperty readyProperty() {
        return ready.getReadOnlyProperty();
    }
}
