package avalanche.view.layers.renderers;

import avalanche.view.Viewport;
import avalanche.view.layers.LayerView;
import javafx.scene.canvas.GraphicsContext;

public interface LayerRenderer
{
    void render(GraphicsContext gc, Viewport vp, LayerView layerView);
}
