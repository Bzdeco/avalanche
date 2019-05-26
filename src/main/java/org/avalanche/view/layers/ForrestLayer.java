package org.avalanche.view.layers;

import las2etin.model.Classification;
import las2etin.model.Coordinates;
import las2etin.model.TerrainCell;

import java.awt.*;

public class ForrestLayer implements TerrainLayer {

    private final String name;

    public ForrestLayer(String name) {
        this.name = name;
    }

    @Override
    public void drawCell(Graphics2D graphics, TerrainCell cell, Coordinates drawCoords, int drawWidth, int drawHeight) {
        Color pixelColor = getColorFromClassification(cell.getClassification());

        Shape cellRectangle = new Rectangle(drawCoords.getX(), drawCoords.getY(), drawWidth, drawHeight);
        graphics.setPaint(pixelColor);
        graphics.draw(cellRectangle);
        graphics.fill(cellRectangle);
    }

    private Color getColorFromClassification(Classification classification) {
        switch (classification) {
            case GROUND:
                return Color.BLACK;
            case FORREST:
                return Color.GREEN;
            default:
                return Color.WHITE;
        }
    }

    @Override
    public String name() {
        return name;
    }
}
