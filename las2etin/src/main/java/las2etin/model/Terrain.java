package las2etin.model;

import tinfour.common.IIncrementalTin;

import java.util.ArrayList;
import java.util.List;

public class Terrain
{
    private final List<TerrainCell> terrainCells;

    private Terrain(List<TerrainCell> terrainCells)
    {
        this.terrainCells = terrainCells;
    }

    public static Terrain fromTIN(IIncrementalTin tin)
    {
        List<TerrainCell> cells = new ArrayList<>();

        // TODO iterate over cells and build Terrain

        return new Terrain(cells);
    }
}
