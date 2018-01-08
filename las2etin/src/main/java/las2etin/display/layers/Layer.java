package las2etin.display;

import las2etin.model.TerrainCell;

import java.awt.*;

public interface LayerPrinter
{
    void drawCell(Graphics2D graphics, TerrainCell cell);
}
