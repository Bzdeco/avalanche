package avalanche.view.layers.renderers;

import avalanche.view.layers.LayerViewport;
import avalanche.view.layers.LayerUI;
import javafx.scene.canvas.GraphicsContext;

public interface LayerRenderer
{
    void render(GraphicsContext gc, LayerViewport vp, LayerUI layerUI);
}
