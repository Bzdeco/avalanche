package avalanche.controller;

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

//        initializeWeather(leDataTask);
//
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
                new FileChooser.ExtensionFilter("Model zserializowany", "*.ser")
        );
        fileChooser.setTitle("Wybierz plik modelu terenu");
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

//    private void initializeWeather(final Task<LeData> dataTask)
//    {
//        avalancheRiskController.prepareAvalanchePredictionTask(
//                dataTask.getValue(),
//                AVALANCHE_RISK_LAYER,
//                HILL_SHADE_LAYER);
//
//        LocalDate now = LocalDate.now();
//        LocalDate wago = now.minus(1, ChronoUnit.WEEKS);
//        fromDate.setValue(wago);
//        toDate.setValue(now);
//
//        WeatherConnector con = WeatherConnector.getInstance();
//
//        EventStreams.changesOf(fromDate.valueProperty())
//                .subscribe(val -> con.buildData(val.getNewValue(), toDate.getValue()));
//        EventStreams.changesOf(toDate.valueProperty())
//                .subscribe(val -> con.buildData(fromDate.getValue(), val.getNewValue()));
//
//        tableView.getSelectionModel()
//                .selectedItemProperty()
//                .addListener((observable, oldValue, newValue) -> {
//                    WeatherDto weatherDto = new WeatherDto.Builder().build((ObservableList<String>) newValue);
//                    avalancheRiskController.executeTask(weatherDto, executorService);
//                });
//        con.setTableView(tableView);
//    }

    public void shutdown() throws InterruptedException
    {
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        executorService.shutdownNow();
    }

//TODO get back to saving stuff
//    public void saveTerrain(MouseEvent mouseEvent)
//    {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model zserializowany", "*.ser"));
//
//        File file = fileChooser.showSaveDialog(null);
//
//        if (file != null) {
//            executorService.execute(new SaveSer(file, terrain.getData()));
//        }
//    }
}
