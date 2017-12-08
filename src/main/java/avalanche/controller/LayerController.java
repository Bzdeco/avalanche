package avalanche.controller;

import avalanche.view.Viewport;
import avalanche.view.layers.LayerView;
import avalanche.view.layers.renderers.GridLayerRenderer;
import avalanche.view.layers.renderers.LayerRenderer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;
import org.reactfx.util.Tuple2;
import org.reactfx.util.Tuples;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class LayerController
{
    private final Viewport viewport;
    private final LayerRenderer layerRenderer = new GridLayerRenderer();

    private List<Tuple2<LayerView, Canvas>> layers = new ArrayList<>();

    private LayerController(final Viewport viewport)
    {
        this.viewport = viewport;
    }

    public static LayerController initializeWithLayers(final List<LayerView> layers,
                                                       final Viewport viewport)
    {
        final LayerController layerController = new LayerController(viewport);
        layerController.registerLayers(layers);
        return layerController;
    }

    public List<Tuple2<LayerView, Canvas>> getLayers()
    {
        return layers;
    }

    private void registerLayers(List<LayerView> layers)
    {
        layers.forEach(this::registerLayer);
    }

    private void registerLayer(LayerView layerView)
    {
        Canvas layerCanvas = new Canvas();
        layerView.isVisibleProperty().bindBidirectional(layerCanvas.visibleProperty());

        layers.add(Tuples.t(layerView, layerCanvas));
        viewport.getChildren().add(0, layerCanvas);
        viewport.setLayers(layers);
        viewport.registerChangeEvents(layerView, layerCanvas);

    }

    public Subscription renderLayers()
    {
        return EventStreams.merge(viewport.getViewportChanges())
                .reduceSuccessions((a, b) -> a, Duration.ofMillis(10))
                .subscribe(__ -> render());
    }

    private void render()
    {
        for (Tuple2<LayerView, Canvas> lc : layers) {
            GraphicsContext gc = lc._2.getGraphicsContext2D();
            gc.clearRect(0, 0, viewport.getWidth(), viewport.getHeight());
            if (lc._1.isVisible() && lc._1.readyProperty().get())
                layerRenderer.render(gc, viewport, lc._1);
        }
    }
}
