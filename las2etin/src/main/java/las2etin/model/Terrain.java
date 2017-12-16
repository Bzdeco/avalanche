package las2etin.model;

import las2etin.las.vertex.Bounds;
import las2etin.tin.Tin;
import tinfour.common.Vertex;
import tinfour.interpolation.GwrTinInterpolator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Terrain implements Serializable
{
    private final List<TerrainCell> terrainCells;
    private final TerrainSettings settings;

    private Terrain(List<TerrainCell> terrainCells, TerrainSettings settings)
    {
        this.terrainCells = terrainCells;
        this.settings = settings;
    }

    public TerrainCell getCellWithCoordinates(int x, int y)
    {
        return terrainCells.get(getIndexForCoordinates(x, y));
    }

    public Terrain create(Tin tin, TerrainSettings settings)
    {
        int widthInCells = settings.getWidthInCells();
        int heightInCells = settings.getHeightInCells();
        int totalNumberOfCells = widthInCells * heightInCells;

        List<TerrainCell> cells = new ArrayList<>(totalNumberOfCells);

        Bounds bounds = tin.getBounds();
        double realCellWidth = bounds.getWidth() / widthInCells;
        double realCellHeight = bounds.getHeight() / heightInCells;
        double widthOffset = realCellWidth / 2;
        double heightOffset = realCellHeight / 2;

        GwrTinInterpolator interpolator = new GwrTinInterpolator(tin.getIncrementalTin());

        for (int x = 0; x < widthInCells; x++) {
            for (int y = 0; y < heightInCells; y++) {
                Vertex interpolatedVertex = new Vertex(widthOffset + x * realCellWidth,
                                                         heightOffset + y * realCellHeight,
                                                         0);
                TerrainCell cell = new TerrainCellBuilder().withInterpolator(interpolator)
                                                           .withVertex(interpolatedVertex)
                                                           .build();
                cells.add(getIndexForCoordinates(x, y), cell);
            }
        }

        return new Terrain(cells, settings);
    }

    private int getIndexForCoordinates(int x, int y)
    {
        return settings.getHeightInCells() * x + y;
    }
}
