package avalanche.controller;

import avalanche.model.database.WeatherConnector;
import avalanche.model.database.WeatherDto;
import avalanche.model.risk.Risk;
import avalanche.ser.display.TerrainPrinter;
import avalanche.ser.display.layers.LandformLayer;
import avalanche.ser.display.layers.Layer;
import avalanche.ser.display.layers.SlopeLayer;
import avalanche.ser.display.layers.SusceptiblePlacesLayer;
import com.google.common.collect.ImmutableList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import las2etin.display.TerrainFormatter;
import las2etin.model.Terrain;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import weatherCollector.coordinates.Coords;
import weatherCollector.coordinates.StaticMapNameToCoordsConverter;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<Layer> LAYERS = ImmutableList.of(
            new LandformLayer("landform"),
            new SlopeLayer("slope"),
            new SusceptiblePlacesLayer("susceptible_places")
    );

    private final AvalancheRiskController avalancheRiskController = new AvalancheRiskController();

    public final StaticMapNameToCoordsConverter converter = new StaticMapNameToCoordsConverter();

    private ExecutorService executorService = Executors.newFixedThreadPool(6);

    @FXML
    public Button centerView;

    @FXML
    private ProgressBar progress;

    @FXML
    private TreeView layerSelector;

    @FXML
    private Pane layerViewport;

    @FXML
    private DatePicker fromDate;

    @FXML
    private DatePicker toDate;

    @FXML
    private Button playBtn;

    @FXML
    private TableView tableView;

    @FXML
    public void initialize()
    {
        final File file = selectFile();

        collectWeatherDataToDatabase(file.getName());

        final Terrain terrain = TerrainFormatter.deserialize(file.toPath());
        new TerrainPrinter(terrain).drawOnPane(layerViewport, LAYERS, layerSelector);

        Coords geographicalCoords = converter.convert(file.getName());
        initializeAvalancheRiskPrediction(terrain, geographicalCoords);

        List<WeatherDto> weatherForecast = fetchWeather();
        avalancheRiskController.addWeather(weatherForecast);

        Risk risk = avalancheRiskController.predict();
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

    private void initializeAvalancheRiskPrediction(final Terrain terrain, Coords geographicalCoordinates) {
        avalancheRiskController.prepareAvalanchePrediction(terrain, geographicalCoordinates);
    }

    private List<WeatherDto> fetchWeather()
    {
        WeatherConnector connector = WeatherConnector.getInstance();
        connector.setTableView(tableView);
        return connector.fetchAndBuildData();
    }

    public void shutdown() throws InterruptedException
    {
    }
}
