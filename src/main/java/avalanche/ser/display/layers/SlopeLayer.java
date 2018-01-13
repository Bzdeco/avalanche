package avalanche.ser.display.layers;

import las2etin.model.TerrainCell;

import java.awt.*;

public class SlopeLayer implements Layer
{
    private static final double MAX_SLOPE_IN_DEGREES = 90;
    private static final float SLOPE_COLOR_HUE = 0f;
    private static final float SLOPE_COLOR_BRIGHTNESS = 1f;

    private final String name;

    public SlopeLayer(final String name)
    {
        this.name = name;
    }

    @Override
    public void drawCell(Graphics2D graphics, TerrainCell cell)
    {
        Color pixelColor = getColorFromSlope(cell.getSlope());

        Shape cellRectangle = new Rectangle(cell.getX(), cell.getY(), 1, 1);
        graphics.setPaint(pixelColor);
        graphics.draw(cellRectangle);
        graphics.fill(cellRectangle);
    }

    @Override
    public String name() {
        return name;
    }

    private Color getColorFromSlope(double slope)
    {
        double slopePercentage = slope / MAX_SLOPE_IN_DEGREES;
        if (isSlopePercentageOutOfRange(slopePercentage)) slopePercentage = 0;

        return Color.getHSBColor(SLOPE_COLOR_HUE, (float) slopePercentage, SLOPE_COLOR_BRIGHTNESS);
    }

    private boolean isSlopePercentageOutOfRange(double slopePercentage)
    {
        return slopePercentage > 1 || slopePercentage < 0;
    }
}
