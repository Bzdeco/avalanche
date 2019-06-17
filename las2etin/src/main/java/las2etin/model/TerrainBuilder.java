package las2etin.model;

import las2etin.tin.Tin;
import org.tinfour.common.IIncrementalTin;
import org.tinfour.common.INeighborhoodPointsCollector;
import org.tinfour.common.Vertex;
import org.tinfour.gis.utils.VertexWithClassification;
import org.tinfour.gwr.GwrTinInterpolator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class TerrainBuilder
{
    public static final int GROUND_CLASSIFICATION = 2;
    public static final int LOW_VEGETATION_CLASSIFICATION = 3;
    public static final int MEDIUM_VEGETATION_CLASSIFICATION = 4;
    public static final int HIGH_VEGETATION_CLASSIFICATION = 5;
    public static final int WATER_CLASSIFICATION = 9;


    private Tin tin;
    private GwrTinInterpolator interpolator;
    private INeighborhoodPointsCollector neighboursCollector;
    private TerrainProperties properties;
	private GeographicBounds geographicBounds;
    private Bounds bounds;

    public TerrainBuilder(Tin tin)
    {
        this.tin = tin;
        IIncrementalTin incrementalTin = tin.getIncrementalTin();
        this.interpolator = new GwrTinInterpolator(incrementalTin);
        this.neighboursCollector = incrementalTin.getNeighborhoodPointsCollector();
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
        List<Vertex> neighbours = neighboursCollector.collectNeighboringVertices(interpolatedVertex.getX(),
                interpolatedVertex.getY(), 4, 20);
        GeographicCoordinates geographicCoords = estimateTerrainCellGeographicCoordinates(xCoord, yCoord);
        return new TerrainCellBuilder().withInterpolator(interpolator)
                                       .withVertex(interpolatedVertex)
                                       .withCoordinates(coordinates)
									   .withGeographicCoords(geographicCoords)
                                       .withClassification(classifyVertexBasedOnNeighbours(neighbours))
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

	private Classification classifyVertexBasedOnNeighbours(List<Vertex> neighbours) {
        short vegetationCount = 0;
        short groundCount = 0;
        short waterCount = 0;
        short other = 0;

        for (Vertex vertex : neighbours) {
            int classification = tryGettingClassificationByDowncasting(vertex);

            switch (classification){
                case HIGH_VEGETATION_CLASSIFICATION: {
                    vegetationCount++;
                    break;
                }
                case GROUND_CLASSIFICATION: {
                    groundCount++;
                    break;
                }
                case WATER_CLASSIFICATION: {
                    waterCount++;
                    break;
                }
                default: {
                    other++;
                }
            }
        }

        if (other > groundCount && other > vegetationCount && other > waterCount) {
            return Classification.OTHER;
        } else if (vegetationCount > groundCount && vegetationCount > waterCount) {
            return Classification.FORREST;
        } else if (groundCount > waterCount){
            return Classification.GROUND;
        } else {
            return Classification.WATER;
        }
    }

    private int tryGettingClassificationByDowncasting(Vertex vertex) {
        VertexWithClassification classifiedVertex;
        try {
            classifiedVertex = (VertexWithClassification) vertex;
        } catch (ClassCastException e) {
            return 0;
        }

        return classifiedVertex.getClassification();
    }
}
