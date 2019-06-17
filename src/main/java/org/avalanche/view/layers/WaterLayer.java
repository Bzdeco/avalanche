package org.avalanche.view.layers;

import las2etin.model.Classification;
import las2etin.model.Coordinates;
import las2etin.model.TerrainCell;
import lombok.Data;

import java.awt.*;

@Data
public class WaterLayer implements  TerrainLayer{
    private final String name;

    public void drawCell(Graphics2D graphics, TerrainCell cell, Coordinates drawCoords, int drawWidth, int drawHeight){
        Color pixelColor = getColoFromClassification(cell.getClassification());

        Shape cellRectangle = new Rectangle(drawCoords.getX(), drawCoords.getY(), drawWidth, drawHeight);
        graphics.setPaint(pixelColor);
        graphics.draw(cellRectangle);
        graphics.fill(cellRectangle);
    }

    private Color getColoFromClassification(Classification classification) {
        switch (classification){
            case GROUND:
                return Color.BLACK;
            case WATER:
                return Color.BLUE;
            default:
                return Color.WHITE;
        }
    }


    @Override
    public String name() {
        return name;
    }
}
