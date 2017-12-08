package avalanche.view.layers.renderers;

import avalanche.view.Viewport;
import avalanche.view.layers.LayerView;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;

public class GridLayerRenderer implements LayerRenderer
{
    @Override
    public void render(final GraphicsContext gc, final Viewport vp, final LayerView layerView)
    {
        float[][][] arr = layerView.getData().getData(); //TODO welp, this sucks :D

        double cellSize = Math.max(1, Math.floor(16 * vp.getZoom()));

        int arrHeight = arr.length;
        int arrWidth = arr[0].length;

        int cellsX = Math.max(0, (int) Math.ceil(vp.getWidth() / cellSize));
        int cellsY = Math.max(0, (int) Math.ceil(vp.getHeight() / cellSize));

        int offX = (arrWidth - cellsX) / 2 + (int) Math.ceil(vp.getPan().getX() / cellSize);
        int offY = (arrHeight - cellsY) / 2 + (int) Math.ceil(vp.getPan().getY() / cellSize);

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
                                pw.setArgb(sX + dx, sY + dy, layerView.convertToColor(row[idxX][layerView.getMagicalIndex()]));
                            }
                        }
                    }
                }
            }
        }
    }

}
