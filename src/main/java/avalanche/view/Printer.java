package avalanche.view;

import avalanche.view.layers.Layer;
import avalanche.view.layers.RiskLayer;
import avalanche.view.layers.TerrainLayer;
import avalanche.view.layers.LayerZoomAndPanUtility;
import avalanche.model.risk.Risk;
import avalanche.model.risk.RiskCell;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import las2etin.model.Coordinates;
import las2etin.model.Terrain;
import las2etin.model.TerrainCell;
import las2etin.model.TerrainProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Optional;

/**
 * This class prints created {@link Layer}s as images and displayes them in the UI.
 */
public class Printer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Printer.class);
    private static final int WIDTH_IN_PIXELS = 500;
    private static final int HEIGHT_IN_PIXELS = 500;

    private final Terrain terrain;
    private final Risk risk;
    private final BufferedImage bufferedImage;
    private final int widthInCells;
    private final int heightInCells;

    public Printer(Terrain terrain, Risk risk)
    {
        this.terrain = terrain;
        this.risk = risk;

        TerrainProperties properties = terrain.getTerrainProperties();
        widthInCells = properties.getWidthInCells();
        heightInCells = properties.getHeightInCells();
        this.bufferedImage = new BufferedImage(WIDTH_IN_PIXELS, HEIGHT_IN_PIXELS, BufferedImage.TYPE_INT_ARGB);
    }

    public void drawOnPane(final Pane pane,
                           final List<TerrainLayer> terrainLayers,
                           final List<RiskLayer> riskLayers,
                           final TreeView layerSelector)
    {
        TreeItem<String> layersRoot = new TreeItem<>("Layers");
        layersRoot.setExpanded(true);

        // Drawing terrain and risk layers in the UI
        terrainLayers.forEach(layer -> drawOnPane(pane, printTerrainLayerToStream(layer), layer, layersRoot));
        riskLayers.forEach(layer -> drawOnPane(pane, printRiskLayerToStream(layer), layer, layersRoot));

        layerSelector.setRoot(layersRoot);
    }

    public void drawOnPane(final Pane pane,
                           final InputStream imageStream,
                           final Layer layer,
                           final TreeItem layerUiController)
    {
        final Image image = new Image(imageStream);
        final ImageView imageView = doDrawOnPane(pane, image);
        LayerZoomAndPanUtility.makeZoomable(image, imageView, pane);
        setupLayerUiController(layer, layerUiController, imageView);
    }

    private InputStream printTerrainLayerToStream(final TerrainLayer terrainLayer)
    {
        Graphics2D graphics = bufferedImage.createGraphics();

        for (int x = 0; x < widthInCells; x++) {
            for (int y = 0; y < heightInCells; y++) {
                Optional<TerrainCell> terrainCell = terrain.getCellWithCoordinates(new Coordinates(x, heightInCells - y - 1));
				int drawWidth = WIDTH_IN_PIXELS / widthInCells;
				int drawHeight = HEIGHT_IN_PIXELS / heightInCells;
				Coordinates drawCoords = new Coordinates(x * drawWidth, y * drawHeight);
				terrainCell.ifPresent(cell -> terrainLayer.drawCell(graphics, cell, drawCoords, drawWidth, drawHeight));
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "PNG", outputStream);
        }
        catch (IOException e) {
            LOGGER.error("Error creating image");
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private InputStream printRiskLayerToStream(final RiskLayer riskLayer)
    {
        Graphics2D graphics = bufferedImage.createGraphics();

        for (int x = 0; x < widthInCells; x++) {
            for (int y = 0; y < heightInCells; y++) {
                Optional<RiskCell> riskCell = risk.getRiskCellWithCoordinates(new Coordinates(x, heightInCells - y - 1));
                int drawWidth = WIDTH_IN_PIXELS / widthInCells;
				int drawHeight = HEIGHT_IN_PIXELS / heightInCells;
				Coordinates drawCoords = new Coordinates(x * drawWidth, y * drawHeight);
				riskCell.ifPresent(cell -> riskLayer.drawCell(graphics, cell, drawCoords, drawWidth, drawHeight));
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "PNG", outputStream);
        }
        catch (IOException e) {
            LOGGER.error("Error creating image");
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
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
        Slider slider = new Slider(0.0, 1.0, 0.50);
        alphaSlider.setGraphic(slider);
        slider.valueProperty().bindBidirectional(imageView.opacityProperty());
        return alphaSlider;
    }
}
