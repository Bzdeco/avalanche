package avalanche.ser.display.layers;

import avalanche.ser.model.TerrainCell;

import java.awt.*;

public interface Layer
{
    void drawCell(Graphics2D graphics, TerrainCell cell);
}
