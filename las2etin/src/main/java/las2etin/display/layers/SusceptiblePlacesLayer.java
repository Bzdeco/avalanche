package las2etin.display.layers;

import las2etin.model.TerrainCell;

import java.awt.*;

// TODO separate logic from plain printing (this layer uses specific values for evaluating risk-like feature)
public class SusceptiblePlacesLayer implements Layer
{
    private static final double MINIMAL_SLOPE = 20;
    private static final double MAXIMAL_SLOPE = 50;
    private static final double MEAN_SLOPE = (MAXIMAL_SLOPE + MINIMAL_SLOPE) / 2;
    private static final double MAX_DIFFERENCE = Math.abs(MAXIMAL_SLOPE - MEAN_SLOPE);
    private static final float SUSCEPTIBILITY_COLOR_HUE = 0f;
    private static final float SUSCEPTIBILITY_COLOR_BRIGHTNESS = 1f;

    public SusceptiblePlacesLayer()
    {
    }

    @Override
    public void drawCell(Graphics2D graphics, TerrainCell cell)
    {
        Color pixelColor = getSusceptibilityColorFromSlope(cell.getSlope());

        Shape cellRectangle = new Rectangle(cell.getX(), cell.getY(), 1, 1);
        graphics.setPaint(pixelColor);
        graphics.draw(cellRectangle);
        graphics.fill(cellRectangle);
    }

    private Color getSusceptibilityColorFromSlope(double slope)
    {
        float value = 0;
        if (slope >= MINIMAL_SLOPE || slope <= MAXIMAL_SLOPE) {
            value = (float) (1 - Math.abs(slope - MEAN_SLOPE) / MAX_DIFFERENCE);
        }

        return Color.getHSBColor(SUSCEPTIBILITY_COLOR_HUE, value, SUSCEPTIBILITY_COLOR_BRIGHTNESS);
    }
}
