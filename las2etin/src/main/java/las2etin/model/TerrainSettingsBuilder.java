package las2etin.model;

import lombok.NoArgsConstructor;

import static com.google.common.base.Preconditions.checkArgument;

@NoArgsConstructor
public class TerrainSettingsBuilder
{
    private int widthInCells;
    private int heightInCells;

    public TerrainSettingsBuilder withWidthInCells(int widthInCells)
    {
        this.widthInCells = widthInCells;
        return this;
    }

    public TerrainSettingsBuilder withHeightInCells(int heightInCells)
    {
        this.heightInCells = heightInCells;
        return this;
    }

    public TerrainSettings build()
    {
        checkArgument(widthInCells > 0);
        checkArgument(heightInCells > 0);

        return new TerrainSettings(widthInCells, heightInCells);
    }
}
