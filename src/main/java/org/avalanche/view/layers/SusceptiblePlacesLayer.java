package org.avalanche.view.layers;

import las2etin.model.Coordinates;
import las2etin.model.TerrainCell;

import java.awt.*;

public class SusceptiblePlacesLayer implements TerrainLayer
{
    private static final double MINIMAL_SLOPE = 30;
    private static final double MAXIMAL_SLOPE = 45;
    private static final float SUSCEPTIBILITY_COLOR_HUE = 0f;
    private static final float SUSCEPTIBILITY_COLOR_BRIGHTNESS = 1f;

    private final String name;

    public SusceptiblePlacesLayer(String name) {
        this.name = name;
    }

    @Override
    public void drawCell(Graphics2D graphics, TerrainCell cell, Coordinates drawCoords, int drawWidth, int drawHeight)
    {
        Color pixelColor = getSusceptibilityColorFromSlope(cell.getSlope());

        Shape cellRectangle = new Rectangle(drawCoords.getX(), drawCoords.getY(), drawWidth, drawHeight);
        graphics.setPaint(pixelColor);
        graphics.draw(cellRectangle);
        graphics.fill(cellRectangle);
    }

    @Override
    public String name() {
        return name;
    }

    private Color getSusceptibilityColorFromSlope(double slope)
    {
        float value = 0;
        if (slope >= MINIMAL_SLOPE && slope <= MAXIMAL_SLOPE)
            value = 1f;

        return Color.getHSBColor(SUSCEPTIBILITY_COLOR_HUE, value, SUSCEPTIBILITY_COLOR_BRIGHTNESS);
    }
}
