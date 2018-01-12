package avalanche.ser.display.layers;

import las2etin.model.TerrainCell;

import java.awt.*;

public interface Layer
{
    void drawCell(Graphics2D graphics, TerrainCell cell);
}
