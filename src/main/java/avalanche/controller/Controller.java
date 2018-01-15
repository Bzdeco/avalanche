package avalanche.controller;

import avalanche.model.database.WeatherConnector;
import avalanche.model.database.WeatherDto;
import avalanche.model.display.Printer;
import avalanche.model.display.layers.*;
import avalanche.model.risk.Risk;
import com.google.common.collect.ImmutableList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import las2etin.display.TerrainFormatter;
import las2etin.model.Terrain;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import weatherCollector.coordinates.Coords;
import weatherCollector.coordinates.StaticMapNameToCoordsConverter;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Controller
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<TerrainLayer> TERRAIN_LAYERS = ImmutableList.of(
            new LandformLayer("Landform"),
            new SlopeLayer("Slope"),
            new SusceptiblePlacesLayer("Susceptible places")
    );
    private static final List<RiskLayer> RISK_LAYERS = ImmutableList.of();

    public final StaticMapNameToCoordsConverter converter = new StaticMapNameToCoordsConverter();

    @FXML
    private MenuItem openFileMenuItem;

    @FXML
    private MenuItem exitMenuItem;

    @FXML
    private ProgressBar globalRisk;

    @FXML
    private TreeView layerSelector;

    @FXML
    private Pane layerViewport;

    @FXML
    private TableView tableView;

    @FXML
    public void initialize()
    {
        final File file = selectFile();

        if (file != null) {
            collectWeatherDataToDatabase(file.getName());

            final Terrain terrain = TerrainFormatter.deserialize(file.toPath());
            final Coords geographicalCoordinates = converter.convert(file.getName());
            AvalancheRiskController avalancheRiskController = new AvalancheRiskController(terrain,
                                                                                          geographicalCoordinates);
            List<WeatherDto> weatherConditions = avalancheRiskController.fetchWeatherDataInto(tableView);

            final Risk risk = avalancheRiskController.getEvaluatedRisk(weatherConditions);

            new Printer(terrain, risk).drawOnPane(layerViewport, TERRAIN_LAYERS, RISK_LAYERS, layerSelector);

            float globalRiskValue = avalancheRiskController.getGlobalRiskValue();
            globalRisk.setProgress(globalRiskValue);
        }
    }

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

    public void shutdown() throws InterruptedException
    {
    }
}
