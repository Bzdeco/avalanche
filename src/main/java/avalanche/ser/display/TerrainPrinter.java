package avalanche.ser.display;

import avalanche.ser.display.layers.Layer;
import avalanche.ser.display.layers.LayerZoomAndPanUtility;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import las2etin.model.Coordinates;
import las2etin.model.Terrain;
import las2etin.model.TerrainCell;
import las2etin.model.TerrainProperties;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TerrainPrinter
{
    private static final String RESOURCE_PATH = "src/main/resources/";
    private static final int MIN_PIXELS = 10;

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

    public void drawOnPane(final Pane pane, final List<Layer> layers, final TreeView layerSelector)
    {
        TreeItem<String> layersRoot = new TreeItem<>("Warstwy");
        layersRoot.setExpanded(true);
        layers.forEach(layer -> drawOnPane(pane, layer, layersRoot));
        layerSelector.setRoot(layersRoot);
    }

    public void drawOnPane(final Pane pane, final Layer layer, final TreeItem layerUiController)
    {
        try {
            printToFile(layer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final Image image = new Image(layer.name() + ".png");
        final ImageView imageView = doDrawOnPane(pane, image);
        LayerZoomAndPanUtility.makeZoomable(image, imageView, pane);
        setupLayerUiController(layer, layerUiController, imageView);
    }

    private void printToFile(final Layer layer) throws IOException
    {
        Graphics2D graphics = bufferedImage.createGraphics();

        for (int x = 0; x < widthInPixels; x++) {
            for (int y = 0; y < heightInPixels; y++) {
                Coordinates coordinates = new Coordinates(x, y);
                Optional<TerrainCell> terrainCell = terrain.getCellWithCoordinates(coordinates);
                terrainCell.ifPresent(cell -> layer.drawCell(graphics, cell));
            }
        }

        ImageIO.write(bufferedImage, "PNG", new File(RESOURCE_PATH + layer.name() + ".png"));
    }

    private ImageView doDrawOnPane(final Pane pane, final Image image)
    {
        final ImageView imageView = new ImageView(image);
        imageView.fitHeightProperty().bind(pane.heightProperty());
        imageView.fitWidthProperty().bind(pane.widthProperty());
        pane.getChildren().add(imageView);
        return imageView;
    }

    private void setupLayerUiController(final Layer layer,
                                        final TreeItem layerUiController,
                                        final ImageView imageView)
    {
        TreeItem<String> layerItem = new TreeItem<>();
        layerItem.valueProperty().bindBidirectional(new SimpleStringProperty(layer.name()));
        layerItem.setGraphic(getLayerToggle(imageView));
        layerItem.getChildren().add(getAlphaSlider(imageView));
        layerUiController.getChildren().add(layerItem);
    }

    private CheckBox getLayerToggle(final ImageView imageView)
    {
        CheckBox layerToggle = new CheckBox();
        layerToggle.selectedProperty().bindBidirectional(imageView.visibleProperty());
        return layerToggle;
    }

    private TreeItem<String> getAlphaSlider(final ImageView imageView)
    {
        TreeItem<String> alphaSlider = new TreeItem<>("Alpha");
        Slider slider = new Slider(0.1, 1.0, 0.50);
        alphaSlider.setGraphic(slider);
        slider.valueProperty().bindBidirectional(imageView.opacityProperty());
        return alphaSlider;
    }
}
