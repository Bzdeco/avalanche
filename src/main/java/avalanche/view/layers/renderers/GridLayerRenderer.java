package avalanche.view.layers.renderers;

import avalanche.view.layers.LayerViewport;
import avalanche.view.layers.LayerUI;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;

public class GridLayerRenderer implements LayerRenderer
{
    @Override
    public void render(final GraphicsContext gc,
                       final LayerViewport viewport,
                       final LayerUI layerUI)
    {
        float[][][] arr = layerUI.getData().getData(); //TODO welp, this sucks :D

        double cellSize = Math.max(1, Math.floor(16 * viewport.getZoom()));

        int arrHeight = arr.length;
        int arrWidth = arr[0].length;

        int cellsX = Math.max(0, (int) Math.ceil(viewport.getWidth() / cellSize));
        int cellsY = Math.max(0, (int) Math.ceil(viewport.getHeight() / cellSize));

        int offX = (arrWidth - cellsX) / 2 + (int) Math.ceil(viewport.getPan().getX() / cellSize);
        int offY = (arrHeight - cellsY) / 2 + (int) Math.ceil(viewport.getPan().getY() / cellSize);

        PixelWriter pw = gc.getPixelWriter();
        final int cs = (int) cellSize;

        for (int y = 0; y < cellsY; ++y) {
            final int idxY = y + offY, sY = y * cs;
            if (idxY >= 0 && idxY < arrHeight) {
                final float[][] row = arr[idxY];
                for (int dy = 0; dy < cs; ++dy) {
                    for (int x = 0; x < cellsX; ++x) {
                        final int idxX = x + offX, sX = x * cs;
                        if (idxX >= 0 && idxX < arrWidth) {
                            for (int dx = 0; dx < cs; ++dx) {
                                pw.setArgb(sX + dx, sY + dy, layerUI.convertToColor(row[idxX][layerUI.getMagicalIndex()]));
                            }
                        }
                    }
                }
            }
        }
    }

}
