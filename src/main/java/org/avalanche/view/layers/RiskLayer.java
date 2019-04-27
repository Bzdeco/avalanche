package org.avalanche.view.layers;

import org.avalanche.model.risk.RiskCell;
import las2etin.model.Coordinates;

import java.awt.*;

public interface RiskLayer extends Layer
{
    void drawCell(Graphics2D graphics, RiskCell cell, Coordinates drawCoords, int drawWidth, int drawHeight);
}
