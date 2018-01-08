package las2etin.output;

import las2etin.las.vertex.Bounds;
import las2etin.model.Terrain;
import las2etin.model.TerrainCell;
import las2etin.model.TerrainProperties;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class TerrainPrinter
{
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;

    private final Terrain terrain;
    private final BufferedImage bufferedImage;

    public TerrainPrinter(Terrain terrain)
    {
        this.terrain = terrain;
        this.bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    }

    public void printLandform() throws IOException
    {
        Graphics2D graphics = bufferedImage.createGraphics();

        TerrainProperties terrainProperties = terrain.getTerrainProperties();
        int widthInCells = terrainProperties.getWidthInCells();
        int heightInCells = terrainProperties.getHeightInCells();
        Bounds bounds = terrain.getBounds();
        double minZ = bounds.getMinZ();
        double zRange = bounds.getMaxZ() - bounds.getMinZ();

        for (int x = 0; x < widthInCells; x++) {
            for (int y = 0; y < heightInCells; y++) {
                Optional<TerrainCell> cell = terrain.getCellWithCoordinates(x, y);
                if (cell.isPresent()) {
                    double altitude = cell.get().getAltitude();
                    float value = 1f - (float) ((altitude - minZ) / zRange);
                    //int value = (int) (grade * 255);
                    if (value > 255 || value < 0) value = 0;
                    graphics.setPaint(Color.getHSBColor(0.40f, 1f, value));
                    Shape cellRectangle = new Rectangle(x, y, 1, 1);
                    graphics.draw(cellRectangle);
                    graphics.fill(cellRectangle);
                }
            }
        }

        ImageIO.write(bufferedImage, "PNG", new File("src/test/resources/test.png"));
    }

    public void printSlope() throws IOException
    {
        Graphics2D graphics = bufferedImage.createGraphics();

        TerrainProperties terrainProperties = terrain.getTerrainProperties();
        int widthInCells = terrainProperties.getWidthInCells();
        int heightInCells = terrainProperties.getHeightInCells();
        Bounds bounds = terrain.getBounds();
        double minZ = bounds.getMinZ();
        double zRange = bounds.getMaxZ() - bounds.getMinZ();

        for (int x = 0; x < widthInCells; x++) {
            for (int y = 0; y < heightInCells; y++) {
                Optional<TerrainCell> cell = terrain.getCellWithCoordinates(x, y);
                if (cell.isPresent()) {
                    double slope = cell.get().getSlope(); // 0 - 90
                    float value = (float) (slope / 90);
                    if (value > 1 || value < 0) value = 0;
                    graphics.setPaint(Color.getHSBColor(0f, value, 1f));
                    Shape cellRectangle = new Rectangle(x, y, 1, 1);
                    graphics.draw(cellRectangle);
                    graphics.fill(cellRectangle);
                }
            }
        }

        ImageIO.write(bufferedImage, "PNG", new File("src/test/resources/test_slope.png"));
    }

    public void printSusceptiblePlaces() throws IOException
    {
        Graphics2D graphics = bufferedImage.createGraphics();

        TerrainProperties terrainProperties = terrain.getTerrainProperties();
        int widthInCells = terrainProperties.getWidthInCells();
        int heightInCells = terrainProperties.getHeightInCells();
        Bounds bounds = terrain.getBounds();
        double minZ = bounds.getMinZ();
        double zRange = bounds.getMaxZ() - bounds.getMinZ();

        for (int x = 0; x < widthInCells; x++) {
            for (int y = 0; y < heightInCells; y++) {
                Optional<TerrainCell> cell = terrain.getCellWithCoordinates(x, y);
                if (cell.isPresent()) {
                    double slope = cell.get().getSlope(); // 0 - 90
                    float value = 0;
                    if (slope >= 20 || slope <= 50) {
                        value = 1f - (float) Math.abs(slope - 35) / 15;
                    }
                    graphics.setPaint(Color.getHSBColor(0f, value, 1f));
                    Shape cellRectangle = new Rectangle(x*10, y*10, 10, 10);
                    graphics.draw(cellRectangle);
                    graphics.fill(cellRectangle);
                }
            }
        }

        ImageIO.write(bufferedImage, "PNG", new File("src/test/resources/test_susceptible.png"));
    }
}
