package avalanche.ser.model;

public class TerrainSettings
{
    private int widthInCells;
    private int heightInCells;

    public TerrainSettings(int widthInCells, int heightInCells)
    {
        this.widthInCells = widthInCells;
        this.heightInCells = heightInCells;
    }

    public int getWidthInCells()
    {
        return widthInCells;
    }

    public int getHeightInCells()
    {
        return heightInCells;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TerrainSettings that = (TerrainSettings) o;

        if (widthInCells != that.widthInCells)
            return false;
        return heightInCells == that.heightInCells;
    }

    @Override
    public int hashCode()
    {
        int result = widthInCells;
        result = 31 * result + heightInCells;
        return result;
    }
}
