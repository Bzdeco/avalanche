package gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;
import org.reactfx.util.Tuple2;
import org.reactfx.util.Tuples;

import java.time.Duration;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Viewport extends Pane {

    private ObservableSet<EventStream<Void>> viewportChanges = FXCollections.observableSet();
    private ArrayList<Tuple2<Layer, Canvas>> layers = new ArrayList<>();
    private DoubleProperty width = new SimpleDoubleProperty(0);
    private DoubleProperty height = new SimpleDoubleProperty(0);
    private DoubleProperty zoom = new SimpleDoubleProperty(1);
    private ObjectProperty<Point2D> pan = new SimpleObjectProperty<>(Point2D.ZERO);

    public Viewport() {
        viewportChanges.addAll(Stream.of(width, height, zoom, pan)
                .map(EventStreams::invalidationsOf)
                .collect(Collectors.toList()));
    }

    public ArrayList<Tuple2<Layer, Canvas>> getLayers() {
        return layers;
    }

    public void registerLayer(Layer layer) {
        Canvas layerCanvas = new Canvas();
        layer.isVisibleProperty().bindBidirectional(layerCanvas.visibleProperty());

        layers.add(Tuples.t(layer, layerCanvas));
        getChildren().add(0, layerCanvas);

        viewportChanges.add(EventStreams.invalidationsOf(layer.isReadyProperty()));
        viewportChanges.add(EventStreams.invalidationsOf(layer.isVisibleProperty()));

        // Fix for not invalidating size on parent component
        if (layers.size() < 2) {
            viewportChanges.add(EventStreams.invalidationsOf(layerCanvas.widthProperty()));
            viewportChanges.add(EventStreams.invalidationsOf(layerCanvas.heightProperty()));
        }
    }

    public Subscription enableRendering() {
        return EventStreams.merge(viewportChanges)
                .reduceSuccessions((a, b) -> a, Duration.ofMillis(10))
                .subscribe(__ -> render());
    }

    private void render() {
        for (Tuple2<Layer, Canvas> lc : layers) {
            GraphicsContext gc = lc._2.getGraphicsContext2D();
            gc.clearRect(0, 0, getWidth(), getHeight());
            if (lc._1.isVisible() && lc._1.isReady()) lc._1.render(gc, this);
        }
    }

    public Point2D getPan() {
        return pan.get();
    }

    public void setPan(Point2D pan) {
        this.pan.set(pan);
    }

    public ObjectProperty<Point2D> panProperty() {
        return pan;
    }

    public double getZoom() {
        return zoom.get();
    }

    public void setZoom(double zoom) {
        this.zoom.set(zoom);
    }

    public DoubleProperty zoomProperty() {
        return zoom;
    }

    @Override
    protected void layoutChildren() {
        final int top = (int) snappedTopInset();
        final int right = (int) snappedRightInset();
        final int bottom = (int) snappedBottomInset();
        final int left = (int) snappedLeftInset();
        final int w = (int) getWidth() - left - right;
        final int h = (int) getHeight() - top - bottom;

        for (Tuple2<Layer, Canvas> lc : layers) {
            lc._2.setLayoutX(left);
            lc._2.setLayoutY(top);
            lc._2.setWidth(w);
            lc._2.setHeight(h);
        }
    }

}
