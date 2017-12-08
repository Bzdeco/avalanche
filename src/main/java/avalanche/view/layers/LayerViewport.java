package avalanche.view.layers;

import avalanche.view.layers.renderers.GridLayerRenderer;
import avalanche.view.layers.renderers.LayerRenderer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LayerViewport extends Pane
{
    private ObservableSet<EventStream<Void>> viewportChanges = FXCollections.observableSet();
    private DoubleProperty width = new SimpleDoubleProperty(0);
    private DoubleProperty height = new SimpleDoubleProperty(0);
    private DoubleProperty zoom = new SimpleDoubleProperty(1);
    private ObjectProperty<Point2D> pan = new SimpleObjectProperty<>(Point2D.ZERO);

    private List<LayerView> layers = new ArrayList<>();
    private final LayerRenderer layerRenderer = new GridLayerRenderer();

    public LayerViewport()
    {
        viewportChanges.addAll(Stream.of(width, height, zoom, pan)
                .map(EventStreams::invalidationsOf)
                .collect(Collectors.toList()));
    }

    public void registerLayers(final List<LayerUI> layers)
    {
        layers.forEach(this::registerLayer);
    }

    private void registerLayer(final LayerUI layerUI)
    {
        Canvas layerCanvas = new Canvas();
        layerUI.isVisibleProperty().bindBidirectional(layerCanvas.visibleProperty());
        layers.add(new LayerView(layerUI, layerCanvas));
        getChildren().add(0, layerCanvas);
        registerChangeEvents(layerUI, layerCanvas);

    }

    private void registerChangeEvents(final LayerUI layerUI,
                                      final Canvas layerCanvas)
    {
        viewportChanges.add(EventStreams.invalidationsOf(layerUI.readyProperty()));
        viewportChanges.add(EventStreams.invalidationsOf(layerUI.isVisibleProperty()));

        // Fix for not invalidating size on parent component
        if (layers.size() < 2) {
            viewportChanges.add(EventStreams.invalidationsOf(layerCanvas.widthProperty()));
            viewportChanges.add(EventStreams.invalidationsOf(layerCanvas.heightProperty()));
        }
    }

    public void createLayerControls(final String viewName,
                                    final TreeView layerSelector)
    {
        TreeItem<String> layersRoot = new TreeItem<>(viewName);
        layersRoot.setExpanded(true);
        layers.forEach(layerView -> feedLayerControls(layerView, layersRoot));
        layerSelector.setRoot(layersRoot);
    }

    private void feedLayerControls(final LayerView layerView,
                                   final TreeItem<String> layersRoot)
    {
        LayerUI layerUI = layerView.getLayerUI();
        CheckBox layerToggle = new CheckBox();
        layerToggle.selectedProperty().bindBidirectional(layerUI.isVisibleProperty());
        ProgressIndicator layerLoadIndicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
        layerLoadIndicator.setPrefWidth(16);
        layerLoadIndicator.setPrefHeight(16);
        EventStreams.valuesOf(layerUI.readyProperty()) // was setting to true in constructor |ReadOnlyBooleanWrapper
                .map(r -> r ? null : layerLoadIndicator)
                .feedTo(layerToggle.graphicProperty());
        TreeItem<String> layerItem = new TreeItem<>();
        layerItem.valueProperty().bindBidirectional(layerUI.nameProperty());
        layerItem.setGraphic(layerToggle);
        TreeItem<String> alphaSlider = new TreeItem<>("Alpha");
        Slider slider = new Slider(0.1, 1.0, 0.50);
        alphaSlider.setGraphic(slider);
        slider.valueProperty().bindBidirectional(layerView.getLayerCanvas().opacityProperty());
        layerItem.getChildren().add(alphaSlider);
        layersRoot.getChildren().add(layerItem);
    }

    public Subscription renderLayers()
    {
        return EventStreams.merge(viewportChanges)
                .reduceSuccessions((a, b) -> a, Duration.ofMillis(10))
                .subscribe(__ -> render());
    }

    private void render()
    {
        layers.forEach(layerView -> {
            GraphicsContext gc = layerView.getLayerCanvas().getGraphicsContext2D();
            gc.clearRect(0, 0, getWidth(), getHeight());
            if (layerView.getLayerUI().isVisible() && layerView.getLayerUI().readyProperty().get())
                layerRenderer.render(gc, this, layerView.getLayerUI());
        });
    }

    public Point2D getPan()
    {
        return pan.get();
    }

    public ObjectProperty<Point2D> panProperty()
    {
        return pan;
    }

    public double getZoom()
    {
        return zoom.get();
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

        layers.stream()
                .map(LayerView::getLayerCanvas)
                .forEach(canvas -> {
                    canvas.setLayoutX(left);
                    canvas.setLayoutY(top);
                    canvas.setWidth(w);
                    canvas.setHeight(h);
                });
    }

}
