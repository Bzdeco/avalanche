package avalanche.controller;

import avalanche.model.database.WeatherDto;
import avalanche.view.Printer;
import avalanche.view.layers.*;
import avalanche.model.risk.Risk;
import com.google.common.collect.ImmutableList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import las2etin.display.TerrainFormatter;
import las2etin.model.GeographicCoordinates;
import las2etin.model.StaticMapNameToGeoBoundsConverter;
import las2etin.model.Terrain;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Main application controller setting the UI, displaying fetched weather and drawing generated terrain and risk
 * images in the UI
 */
public class Controller
{
    private static final Logger LOGGER = LogManager.getLogger();
    private List<TerrainLayer> terrainLayers;
    private List<RiskLayer> riskLayers = ImmutableList.of();

    public final StaticMapNameToGeoBoundsConverter converter = new StaticMapNameToGeoBoundsConverter();

//    @FXML
//    private ProgressBar globalRisk;

    @FXML
    private TreeView layerSelector;

    @FXML
    private Pane layerViewport;

    @FXML
    private TableView tableView;

    /**
     * Method called when main application window is launched
     */
    @FXML
    public void initialize()
    {
        final File file = selectFile();

        if (file != null) {
            collectWeatherDataToDatabase(file.getName());

            final Terrain terrain = TerrainFormatter.deserialize(file.toPath());
            final GeographicCoordinates centerCoords = terrain.getCenterCoords();
            AvalancheRiskController avalancheRiskController = new AvalancheRiskController(terrain,
                                                                                          centerCoords);
            terrainLayers = ImmutableList.of(
                    new LandformLayer("Landform"),
                    new SlopeLayer("Slope"),
                    new SusceptiblePlacesLayer("Susceptible places"),
                    new HillshadeLayer("Hillshade", centerCoords)
            );

            riskLayers = ImmutableList.of(
                    new AvalancheRiskLayer("Avalanche risk")
            );

            List<WeatherDto> weatherConditions = avalancheRiskController.fetchWeatherDataInto(tableView);
            final Risk risk = avalancheRiskController.getEvaluatedRisk(weatherConditions);

            new Printer(terrain, risk).drawOnPane(layerViewport, terrainLayers, riskLayers, layerSelector);
//            float globalRiskValue = avalancheRiskController.getGlobalRiskValue();
//            globalRisk.setProgress(globalRiskValue);
        }
    }

    /**
     * Gets most recent 5-day weather forecast to local database
     */
    private void collectWeatherDataToDatabase(String filename)
    {
        OkHttpClient avalancheClient = new OkHttpClient();
        String requestURL = String.format("http://localhost:8080/getWeatherData?filename=%s", filename);
        Request request = new Request.Builder()
                .url(requestURL)
                .build();

        try {
            avalancheClient.newCall(request).execute();
        }
        catch (IOException e) {
            LOGGER.warn("Could not update weather data in the database");
        }
    }

    private File selectFile() {
        try {
            return trySelectFile();

        } catch (OperationNotSupportedException ex) {
            Platform.exit();
            return null;
        }
    }

    private File trySelectFile() throws OperationNotSupportedException
    {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Serialized terrain model", "*.ser"));
        fileChooser.setTitle("Choose serialized terrain model file (.ser)");
        final File file = fileChooser.showOpenDialog(null);
        validateFileSelection(file);
        return file;
    }

    private void validateFileSelection(final File file) throws OperationNotSupportedException
    {
        if (file == null || !file.exists()) {
            LOGGER.error("User cancelled file selection");
            throw new OperationNotSupportedException("You have to select a file to proceed");
        }
    }
}
