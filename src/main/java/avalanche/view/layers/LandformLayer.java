package avalanche.view.layers;

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
    public void drawCell(Graphics2D graphics, TerrainCell cell)
    {
        Color pixelColor = getColorFromAltitude(cell.getAltitude());

        Shape cellRectangle = new Rectangle(cell.getX(), cell.getY(), 1, 1);
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
