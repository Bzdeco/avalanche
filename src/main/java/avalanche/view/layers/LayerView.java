package avalanche.view.layers;

import javafx.scene.canvas.Canvas;

public class LayerView
{
    private final LayerUI layerUI;
    private final Canvas layerCanvas;

    public LayerView(final LayerUI layerUI, final Canvas layerCanvas)
    {
        this.layerUI = layerUI;
        this.layerCanvas = layerCanvas;
    }

    public LayerUI getLayerUI()
    {
        return layerUI;
    }

    public Canvas getLayerCanvas()
    {
        return layerCanvas;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final LayerView layerView = (LayerView) o;

        if (layerUI != null ? !layerUI.equals(layerView.layerUI) : layerView.layerUI != null) return false;
        return layerCanvas != null ? layerCanvas.equals(layerView.layerCanvas) : layerView.layerCanvas == null;
    }

    @Override
    public int hashCode()
    {
        int result = layerUI != null ? layerUI.hashCode() : 0;
        result = 31 * result + (layerCanvas != null ? layerCanvas.hashCode() : 0);
        return result;
    }
}
