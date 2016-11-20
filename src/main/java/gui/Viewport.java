package gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;
import org.reactfx.SuspendableNo;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Viewport {
    private GraphicsContext gc;

    private SuspendableNo repaintOccurs = new SuspendableNo();
    private ObservableSet<EventStream<Void>> viewportChanges = FXCollections.observableSet();

    public Viewport(GraphicsContext gc) {
        this.gc = gc;
        viewportChanges.addAll(Stream.of(width, height, zoom, pan)
                                     .map(a -> EventStreams.invalidationsOf(a))
                                     .collect(Collectors.toList()));
    }

    public ArrayList<Layer> getLayers() {
        return layers;
    }

    public void registerLayer(Layer layer) {
        layers.add(layer);
        viewportChanges.add(EventStreams.invalidationsOf(layer.dataProperty()));
        viewportChanges.add(EventStreams.invalidationsOf(layer.isVisibleProperty()));
        viewportChanges.add(EventStreams.invalidationsOf(layer.colorProperty()));
    }

    public Subscription enableRendering() {
        return EventStreams.merge(viewportChanges)
                    // .emitOn(repaintOccurs.noes()) FIXME
                    .subscribe(__ -> render());
    }

    private int tmpTest = 0;
    private void render() {
        repaintOccurs.suspendWhile(() -> {
            gc.clearRect(0, 0, width.get(), height.get());

            layers.forEach(l -> {
                if (l.isVisible()) l.render(gc, this);
            });

            gc.setFill(Color.BLACK);
            gc.fillText(String.valueOf(tmpTest++), 10, 10);


            gc.strokeLine(width.get() / 2, height.get() / 2 - 10,
                          width.get() / 2, height.get() / 2 + 10);
            gc.strokeLine(width.get() / 2 - 10, height.get() / 2,
                          width.get() / 2 + 10, height.get() / 2);
        });
    }

    private ArrayList<Layer> layers = new ArrayList<>();

    private DoubleProperty width = new SimpleDoubleProperty(0);
    private DoubleProperty height = new SimpleDoubleProperty(0);
    private DoubleProperty zoom = new SimpleDoubleProperty(1);

    private ObjectProperty<Point2D> pan = new SimpleObjectProperty<>(Point2D.ZERO);

    public Point2D getPan() { return pan.get(); }
    public ObjectProperty<Point2D> panProperty() { return pan; }
    public void setPan(Point2D pan) { this.pan.set(pan); }

    public double getWidth() {
        return width.get();
    }
    public DoubleProperty widthProperty() {
        return width;
    }
    public void setWidth(double width) {
        this.width.set(width);
    }

    public double getHeight() {
        return height.get();
    }
    public DoubleProperty heightProperty() {
        return height;
    }
    public void setHeight(double height) {
        this.height.set(height);
    }

    public double getZoom() {
        return zoom.get();
    }
    public DoubleProperty zoomProperty() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom.set(zoom);
    }

}
