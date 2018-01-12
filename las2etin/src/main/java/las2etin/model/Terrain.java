package las2etin.model;

import java.io.Serializable;
import java.util.*;

public class Terrain implements Serializable
{
    private static final long serialVersionUID = -7848043410735605796L;

    private final Map<Integer, List<TerrainCell>> terrainCells;
    private final TerrainProperties terrainProperties;
    private final Bounds bounds;

    Terrain(Map<Integer, List<TerrainCell>> terrainCells, TerrainProperties terrainProperties, Bounds bounds)
    {
        this.terrainCells = terrainCells;
        this.terrainProperties = terrainProperties;
        this.bounds = bounds;
    }

    public TerrainProperties getTerrainProperties()
    {
        return terrainProperties;
    }

    public Bounds getBounds()
    {
        return bounds;
    }

    public Optional<TerrainCell> getCellWithCoordinates(Coordinates coordinates)
    {
        int x = coordinates.getX();
        int y = coordinates.getY();

        List<TerrainCell> searchedRow = terrainCells.getOrDefault(x, new ArrayList<>());
        if (isColumnPresentInRow(y, searchedRow)) {
            return Optional.of(searchedRow.get(y));
        }
        else {
            return Optional.empty();
        }
    }

    private boolean isColumnPresentInRow(int columnIndex, List<TerrainCell> searchedRow)
    {
        return columnIndex <= searchedRow.size();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Terrain terrain = (Terrain) o;

        if (!terrainCells.equals(terrain.terrainCells))
            return false;
        return terrainProperties.equals(terrain.terrainProperties);
    }

    @Override
    public int hashCode()
    {
        int result = terrainCells.hashCode();
        result = 31 * result + terrainProperties.hashCode();
        return result;
    }
}
