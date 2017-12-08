package avalanche.view;

import avalanche.view.layers.LayerView;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.util.Tuple2;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Viewport extends Pane
{
    private ObservableSet<EventStream<Void>> viewportChanges = FXCollections.observableSet();
    private DoubleProperty width = new SimpleDoubleProperty(0);
    private DoubleProperty height = new SimpleDoubleProperty(0);
    private DoubleProperty zoom = new SimpleDoubleProperty(1);
    private ObjectProperty<Point2D> pan = new SimpleObjectProperty<>(Point2D.ZERO);

    private List<Tuple2<LayerView, Canvas>> layers;

    public Viewport()
    {
        viewportChanges.addAll(Stream.of(width, height, zoom, pan)
                .map(EventStreams::invalidationsOf)
                .collect(Collectors.toList()));
    }

    public void setLayers(final List<Tuple2<LayerView, Canvas>> layers)
    {
        this.layers = layers;
    }

    public void registerChangeEvents(final LayerView layerView,
                                     final Canvas layerCanvas)
    {
        viewportChanges.add(EventStreams.invalidationsOf(layerView.readyProperty()));
        viewportChanges.add(EventStreams.invalidationsOf(layerView.isVisibleProperty()));

        // Fix for not invalidating size on parent component
        if (layers.size() < 2) {
            viewportChanges.add(EventStreams.invalidationsOf(layerCanvas.widthProperty()));
            viewportChanges.add(EventStreams.invalidationsOf(layerCanvas.heightProperty()));
        }
    }

    public ObservableSet<EventStream<Void>> getViewportChanges()
    {
        return viewportChanges;
    }

    public Point2D getPan()
    {
        return pan.get();
    }

    public void setPan(Point2D pan)
    {
        this.pan.set(pan);
    }

    public ObjectProperty<Point2D> panProperty()
    {
        return pan;
    }

    public double getZoom()
    {
        return zoom.get();
    }

    public void setZoom(double zoom)
    {
        this.zoom.set(zoom);
    }

    public DoubleProperty zoomProperty()
    {
        return zoom;
    }

    @Override
    protected void layoutChildren()
    {
        final int top = (int) snappedTopInset();
        final int right = (int) snappedRightInset();
        final int bottom = (int) snappedBottomInset();
        final int left = (int) snappedLeftInset();
        final int w = (int) getWidth() - left - right;
        final int h = (int) getHeight() - top - bottom;

        for (Tuple2<LayerView, Canvas> lc : layers) {
            lc._2.setLayoutX(left);
            lc._2.setLayoutY(top);
            lc._2.setWidth(w);
            lc._2.setHeight(h);
        }
    }

}
