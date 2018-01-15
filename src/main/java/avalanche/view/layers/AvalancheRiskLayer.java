package avalanche.view.layers;

import avalanche.model.risk.RiskCell;

import java.awt.*;

/**
 * Layer for displaying avalanche risk.
 */
public class AvalancheRiskLayer implements RiskLayer
{
    private static final float SUSCEPTIBILITY_COLOR_HUE = 0f;
    private static final float SUSCEPTIBILITY_COLOR_BRIGHTNESS = 1f;

    private final String name;

    public AvalancheRiskLayer(String name)
    {
        this.name = name;
    }

    @Override
    public void drawCell(Graphics2D graphics, RiskCell cell)
    {
        Color pixelColor = getColorFromRiskValue(cell.getRiskValue());

        Shape cellRectangle = new Rectangle(cell.getX(), cell.getY(), 1, 1);
        graphics.setPaint(pixelColor);
        graphics.draw(cellRectangle);
        graphics.fill(cellRectangle);
    }

    private Color getColorFromRiskValue(float riskValue)
    {
        return Color.getHSBColor(SUSCEPTIBILITY_COLOR_HUE, riskValue, SUSCEPTIBILITY_COLOR_BRIGHTNESS);
    }

    @Override
    public String name()
    {
        return "Avalanche risk";
    }
}
