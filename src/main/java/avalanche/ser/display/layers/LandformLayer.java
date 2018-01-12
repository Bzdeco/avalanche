package avalanche.ser.display.layers;

import las2etin.model.TerrainCell;

import java.awt.*;

public class LandformLayer implements Layer
{
    private static final double MIN_LEVEL = 0.0;
    private static final double LEVEL_RANGE = 2499.0;
    private static final float LEVEL_COLOR_HUE = 0.4f;
    private static final float LEVEL_COLOR_SATURATION = 1f;

    public LandformLayer()
    {
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

    // TODO return color ranging in shades used on traditional maps
    private Color getColorFromAltitude(double altitude)
    {
        float value = 1f - (float) ((altitude - MIN_LEVEL) / LEVEL_RANGE);
        if (value > 255 || value < 0) value = 0;

        return Color.getHSBColor(LEVEL_COLOR_HUE, LEVEL_COLOR_SATURATION, value);
    }
}
