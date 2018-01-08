package las2etin.display;

import las2etin.display.layers.Layer;
import las2etin.las.vertex.Bounds;
import las2etin.model.Coordinates;
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
    private final Terrain terrain;
    private final BufferedImage bufferedImage;
    private final int widthInPixels;
    private final int heightInPixels;

    public TerrainPrinter(Terrain terrain)
    {
        this.terrain = terrain;

        TerrainProperties properties = terrain.getTerrainProperties();
        widthInPixels = properties.getWidthInCells();
        heightInPixels = properties.getHeightInCells();
        this.bufferedImage = new BufferedImage(widthInPixels, heightInPixels, BufferedImage.TYPE_INT_ARGB);
    }

    public void print(Layer layer) throws IOException {

        Graphics2D graphics = bufferedImage.createGraphics();

        for (int x = 0; x < widthInPixels; x++) {
            for (int y = 0; y < heightInPixels; y++) {
                Coordinates coordinates = new Coordinates(x, y);
                Optional<TerrainCell> terrainCell = terrain.getCellWithCoordinates(coordinates);
                terrainCell.ifPresent(cell -> layer.drawCell(graphics, cell));
            }
        }

        ImageIO.write(bufferedImage, "PNG", new File("src/test/resources/test.png"));
    }
}
