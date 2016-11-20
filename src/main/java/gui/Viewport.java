package gui;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import layers.GridLayer;
import org.reactfx.Change;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;

import java.time.Duration;
import java.util.ArrayList;

public class Viewport {
    private LayerCanvas cv;
    private GraphicsContext gc;

    public Viewport(GraphicsContext gc) {
        final long latency = 10;

        this.gc = gc;

        EventStream<Change<Number>> sizeChanges = EventStreams.merge(
                EventStreams.changesOf(width),
                EventStreams.changesOf(height)
            ).reduceSuccessions((a, b) -> a, Duration.ofMillis(latency));

        EventStream<Change<Number>> zoomChanges = EventStreams.changesOf(zoom);

        EventStream<Change<Number>> panChanges = EventStreams.merge(
                EventStreams.changesOf(panX),
                EventStreams.changesOf(panY)
        ).reduceSuccessions((a, b) -> a, Duration.ofMillis(latency));

        EventStreams.merge(sizeChanges, zoomChanges, panChanges).subscribe(__ -> { render(); });
    }

    public ArrayList<Layer> getLayers() {
        return layers;
    }

    public void registerLayer(Layer layer) {
        layers.add(layer);
        EventStreams.changesOf(layer.isVisibleProperty()).subscribe(__ -> { render(); });
    }

    private void render() {
        gc.clearRect(0, 0, width.get(), height.get());

        layers.forEach(l -> {
            if (l.isVisible()) l.render(gc, this);
        });
    }

    private ArrayList<Layer> layers = new ArrayList<>();

    private DoubleProperty width = new SimpleDoubleProperty(0);
    private DoubleProperty height = new SimpleDoubleProperty(0);
    private DoubleProperty zoom = new SimpleDoubleProperty(1);

    private DoubleProperty panX = new SimpleDoubleProperty(0);
    private DoubleProperty panY = new SimpleDoubleProperty(0);

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

    public double getPanX() {
        return panX.get();
    }

    public DoubleProperty panXProperty() {
        return panX;
    }

    public double getPanY() {
        return panY.get();
    }

    public DoubleProperty panYProperty() {
        return panY;
    }

    public void setPanY(double panY) {
        this.panY.set(panY);
    }

}
