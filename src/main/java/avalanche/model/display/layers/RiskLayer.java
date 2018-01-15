package avalanche.model.display.layers;

import avalanche.model.risk.RiskCell;

import java.awt.*;

public interface RiskLayer extends Layer
{
    void drawCell(Graphics2D graphics, RiskCell cell);
}
