package avalanche.view.layers;

import las2etin.model.Coordinates;
import las2etin.model.TerrainCell;

import java.awt.*;

public class LandformLayer implements TerrainLayer
{
    private static final float MAX_ALTITUDE = 2499.0f;

    private final String name;

    public LandformLayer(final String name)
    {
        this.name = name;
    }

    @Override
    public void drawCell(Graphics2D graphics, TerrainCell cell, Coordinates drawCoords, int drawWidth, int drawHeight)
    {
        Color pixelColor = getColorFromAltitude(cell.getGeographicCoords().getAltitude());

        Shape cellRectangle = new Rectangle(drawCoords.getX(), drawCoords.getY(), drawWidth, drawHeight);
        graphics.setPaint(pixelColor);
        graphics.draw(cellRectangle);
        graphics.fill(cellRectangle);
    }

    @Override
    public String name() {
        return name;
    }

    private Color getColorFromAltitude(double altitude)
    {
        float value = (float) (1f - altitude / MAX_ALTITUDE);
        return ColorRamp.getColorForValue(value);
    }
}
