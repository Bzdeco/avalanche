package avalanche.ser.model;

import java.io.Serializable;

public class TerrainProperties implements Serializable
{
    private final int widthInCells;
    private final int heightInCells;
    private final int numberOfCells;
    private final double realCellWidth;
    private final double realCellHeight;

    public TerrainProperties(int widthInCells, int heightInCells, double realCellWidth, double realCellHeight)
    {
        this.widthInCells = widthInCells;
        this.heightInCells = heightInCells;
        this.numberOfCells = widthInCells * heightInCells;
        this.realCellWidth = realCellWidth;
        this.realCellHeight = realCellHeight;
    }

    public int getWidthInCells()
    {
        return widthInCells;
    }

    public int getHeightInCells()
    {
        return heightInCells;
    }

    public int getNumberOfCells()
    {
        return numberOfCells;
    }

    public double getRealCellWidth()
    {
        return realCellWidth;
    }

    public double getRealCellHeight()
    {
        return realCellHeight;
    }

    public double getWidthOffset()
    {
        return getRealCellWidth() / 2;
    }

    public double getHeightOffset()
    {
        return getRealCellHeight() / 2;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TerrainProperties that = (TerrainProperties) o;

        if (widthInCells != that.widthInCells)
            return false;
        if (heightInCells != that.heightInCells)
            return false;
        if (numberOfCells != that.numberOfCells)
            return false;
        if (Double.compare(that.realCellWidth, realCellWidth) != 0)
            return false;
        return Double.compare(that.realCellHeight, realCellHeight) == 0;
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        result = widthInCells;
        result = 31 * result + heightInCells;
        result = 31 * result + numberOfCells;
        temp = Double.doubleToLongBits(realCellWidth);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(realCellHeight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
