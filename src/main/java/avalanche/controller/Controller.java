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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import weatherCollector.coordinates.Coords;
import weatherCollector.coordinates.StaticMapNameToCoordsConverter;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Controller
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<Layer> LAYERS = ImmutableList.of(
            new LandformLayer("landform"),
            new SlopeLayer("slope"),
            new SusceptiblePlacesLayer("susceptible_places")
    );

    private final AvalancheRiskController avalancheRiskController = new AvalancheRiskController();
    //private ExecutorService executorService = Executors.newFixedThreadPool(6);
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
        final Terrain terrain = TerrainFormatter.deserialize(file.toPath());
        new TerrainPrinter(terrain).drawOnPane(layerViewport, LAYERS, layerSelector);

        Coords terrainCoords = converter.convert(file.getName());
        initializeAvalancheRiskPrediction(terrain, terrainCoords);
        List<WeatherDto> weatherDtoList = initializeWeather();
        avalancheRiskController.addWeather(weatherDtoList);
        Risk risk = avalancheRiskController.predict(terrain);
    }

    private File selectFile() {
        try {
            return trySelectFile();

        } catch (OperationNotSupportedException ex) {
            //TODO handle this better in the UI!
            Platform.exit();
            throw new IllegalStateException("You fucked up boi");
        }
    }

    private File trySelectFile() throws OperationNotSupportedException
    {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Serialized terrain model", "*.ser")
        );
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

    private List<WeatherDto> initializeWeather()
    {
        WeatherConnector connector = WeatherConnector.getInstance();
        connector.setTableView(tableView);
        return connector.buildData();
    }

    private void initializeAvalancheRiskPrediction(final Terrain terrain, Coords terrainCoords) {
        avalancheRiskController.prepareAvalanchePrediction(terrain, terrainCoords);
    }

    public void shutdown() throws InterruptedException
    {
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        executorService.shutdownNow();
    }
}
