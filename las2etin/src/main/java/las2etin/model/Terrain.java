package las2etin.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.*;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class Terrain implements Serializable
{
    private static final long serialVersionUID = -7848043410735605796L;

    private final Map<Integer, List<TerrainCell>> terrainCells;
    private final TerrainProperties terrainProperties;
    private final GeographicBounds geographicBounds;
    private final Bounds bounds;

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

    public GeographicCoordinates getCenterCoords()
    {
        return geographicBounds.getCenterCoords();
    }

    private boolean isColumnPresentInRow(int columnIndex, List<TerrainCell> searchedRow)
    {
        return columnIndex <= searchedRow.size();
    }
}
