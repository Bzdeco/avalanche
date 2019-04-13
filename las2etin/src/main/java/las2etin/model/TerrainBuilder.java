package las2etin.model;

import las2etin.tin.Tin;
import org.tinfour.common.Vertex;
import org.tinfour.gwr.GwrTinInterpolator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class TerrainBuilder
{
    private Tin tin;
    private GwrTinInterpolator interpolator;
    private TerrainProperties properties;
	private GeographicBounds geographicBounds;
    private Bounds bounds;

    public TerrainBuilder(Tin tin)
    {
        this.tin = tin;
        this.interpolator = new GwrTinInterpolator(tin.getIncrementalTin());
    }

    public TerrainBuilder withSettings(TerrainSettings settings)
    {
        int widthInCells = settings.getWidthInCells();
        int heightInCells = settings.getHeightInCells();

        this.bounds = tin.getBounds();
        double realCellWidth = bounds.getWidth() / widthInCells;
        double realCellHeight = bounds.getHeight() / heightInCells;

        // TODO check

        this.properties = new TerrainProperties(widthInCells, heightInCells, realCellWidth, realCellHeight);
        return this;
    }

    public TerrainBuilder withGeographicBounds(GeographicBounds geographicBounds)
	{
		this.geographicBounds = geographicBounds;
		return this;
	}

    public Terrain build()
    {
        checkNotNull(properties);

        Map<Integer, List<TerrainCell>> cells = createAllTerrainCells();
        geographicBounds.setCenterAltitude(getCenterAltitude(cells));

        return new Terrain(cells, properties, geographicBounds, tin.getBounds());
    }

	private double getCenterAltitude(Map<Integer, List<TerrainCell>> cells)
	{
		return cells.get((properties.getWidthInCells() / 2)).get(properties.getHeightInCells() / 2).getGeographicCoords().getAltitude();
	}

	private Map<Integer, List<TerrainCell>> createAllTerrainCells()
    {
        int widthInCells = properties.getWidthInCells();
        int heightInCells = properties.getHeightInCells();

        Map<Integer, List<TerrainCell>> cells = new HashMap<>();
        for (int x = 0; x < widthInCells; x++) {
            for (int y = 0; y < heightInCells; y++) {
                TerrainCell cell = createTerrainCell(x, y);
                cells.computeIfAbsent(x, addedRow -> cells.put(addedRow, new ArrayList<>()));
                cells.get(x).add(y, cell);
            }
        }
        return cells;
    }

    private TerrainCell createTerrainCell(int xCoord, int yCoord)
    {
        Coordinates coordinates = new Coordinates(xCoord, yCoord);
        Vertex interpolatedVertex = createVertexForInterpolation(coordinates);
        GeographicCoordinates geographicCoords = estimateTerrainCellGeographicCoordinates(xCoord, yCoord);
        return new TerrainCellBuilder().withInterpolator(interpolator)
                                       .withVertex(interpolatedVertex)
                                       .withCoordinates(coordinates)
									   .withGeographicCoords(geographicCoords)
                                       .build();
    }

	private Vertex createVertexForInterpolation(Coordinates coordinates)
    {
        return new Vertex(
            bounds.getMinX() + properties.getWidthOffset() + coordinates.getX() * properties.getRealCellWidth(),
            bounds.getMinY() + properties.getHeightOffset() + coordinates.getY() * properties.getRealCellHeight(),
            0);
    }

	private GeographicCoordinates estimateTerrainCellGeographicCoordinates(int xCoord, int yCoord)
	{
		int widthInCells = properties.getWidthInCells();
		int heightInCells = properties.getHeightInCells();

		float width = geographicBounds.getWidth();
		float height = geographicBounds.getHeight();

		return new GeographicCoordinates(geographicBounds.getMinLatitude() + (float) (xCoord + 0.5) / widthInCells * width,
								geographicBounds.getMinLongitude() + (float) (yCoord + 0.5) / heightInCells * height);
	}
}
