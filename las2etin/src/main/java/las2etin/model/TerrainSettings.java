package las2etin.model;

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
}
