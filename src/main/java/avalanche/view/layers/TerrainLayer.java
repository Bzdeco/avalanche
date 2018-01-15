package avalanche.view.layers;

import las2etin.model.TerrainCell;

import java.awt.*;

public interface TerrainLayer extends Layer
{
    void drawCell(Graphics2D graphics, TerrainCell cell);
}
