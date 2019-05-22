package org.avalanche.view.layers;


import javafx.scene.paint.Color;
import las2etin.model.Coordinates;
import org.avalanche.model.risk.RiskCell;

import java.awt.*;

/**
 * Layer for displaying avalanche risk.
 */
public class AvalancheRiskLayer implements RiskLayer
{

	private final String name;

	public AvalancheRiskLayer(String name)
	{
		this.name = name;
	}

	@Override
	public void drawCell(Graphics2D graphics, RiskCell cell, Coordinates drawCoords, int drawWidth, int drawHeight)
	{
		Color pixelColor = getColorFromRiskValue(cell.getRiskValue());

		Shape cellRectangle = new Rectangle(drawCoords.getX(), drawCoords.getY(), drawWidth, drawHeight);
        graphics.setPaint(toAwtColor(pixelColor));
		graphics.draw(cellRectangle);
		graphics.fill(cellRectangle);
	}

	private Color getColorFromRiskValue(float riskValue)
	{
		if (riskValue == 0) return Color.WHITE;
		double hue = Color.YELLOW.getHue() - (Color.YELLOW.getHue() - Color.RED.getHue()) * riskValue / 1.0;
		return Color.hsb(hue, 1.0, 1.0);
	}

	@Override
	public String name()
	{
		return "Avalanche risk";
	}

	private java.awt.Color toAwtColor(final Color fxColor) {
		return new java.awt.Color((float) fxColor.getRed(), (float) fxColor.getGreen(), (float) fxColor.getBlue());
	}
}
