package las2etin.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
public class TerrainProperties implements Serializable
{
    private static final long serialVersionUID = -979805754018662669L;

    private final int widthInCells;
    private final int heightInCells;
    private final int numberOfCells;
    private final double realCellWidth;
    private final double realCellHeight;

    TerrainProperties(int widthInCells, int heightInCells, double realCellWidth, double realCellHeight)
    {
        this.widthInCells = widthInCells;
        this.heightInCells = heightInCells;
        this.numberOfCells = widthInCells * heightInCells;
        this.realCellWidth = realCellWidth;
        this.realCellHeight = realCellHeight;
    }

    double getWidthOffset()
    {
        return getRealCellWidth() / 2;
    }

    double getHeightOffset()
    {
        return getRealCellHeight() / 2;
    }
}
