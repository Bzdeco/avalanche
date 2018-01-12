package avalanche.controller;

import avalanche.model.LeData;
import avalanche.model.database.WeatherConnector;
import avalanche.model.database.WeatherDto;
import avalanche.model.fileprocessors.SerFileProcessor;
import avalanche.ser.TerrainFormatter;
import avalanche.ser.display.TerrainPrinter;
import avalanche.ser.display.layers.LandformLayer;
import avalanche.ser.model.Terrain;
import avalanche.view.layers.AvalancheRiskLayer;
import avalanche.view.layers.CurvatureLayer;
import avalanche.view.layers.GradeLayer;
import avalanche.view.layers.HillShadeLayer;
import avalanche.view.layers.LayerViewport;
import avalanche.view.layers.TerrainAltitudeLayer;
import com.sun.javafx.util.Utils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reactfx.EventStreams;
import org.reactfx.StateMachine;
import org.reactfx.util.Tuples;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Controller
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final CurvatureLayer CURVATURE_LAYER = new CurvatureLayer("Krzywizna terenu");
    private static final GradeLayer GRADE_LAYER = new GradeLayer("Nachylenie terenu");
    private static final HillShadeLayer HILL_SHADE_LAYER = new HillShadeLayer("Zacienienie");
    private static final TerrainAltitudeLayer TERRAIN_ALTITUDE_LAYER = new TerrainAltitudeLayer("Wysokośc terenu");
    private static final AvalancheRiskLayer AVALANCHE_RISK_LAYER = new AvalancheRiskLayer("Ryzyko lawinowe");
    private static final String LAYER_VIEW_NAME = "Warstwy";
    private static final String LANDFORM_LAYER_PATH = "src/main/resources/landform.png";

    private final AvalancheRiskController avalancheRiskController = new AvalancheRiskController();
    private ExecutorService executorService = Executors.newFixedThreadPool(6);

    @FXML
    public Button centerView;

    @FXML
    private ProgressBar progress;

    @FXML
    private TreeView layerSelector;

    @FXML
    private LayerViewport layerViewport;

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
//        layerViewport.registerLayers(ImmutableList.of(
//                CURVATURE_LAYER,
//                GRADE_LAYER,
//                HILL_SHADE_LAYER,
//                TERRAIN_ALTITUDE_LAYER,
//                AVALANCHE_RISK_LAYER
//        ));
//
//        final Task<LeData> leDataTask = tryLoadingData();
        // TODO select file .ser
        final File file = selectFile();

        // TODO deserialize to Terrain
        final Terrain terrain = TerrainFormatter.deserialize(file.toPath());

        // TODO display png
        final TerrainPrinter terrainPrinter = new TerrainPrinter(terrain);
        try {
            terrainPrinter.print(new LandformLayer(), LANDFORM_LAYER_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }


//        final Task<LeData> leDataTask = tryLoadingData();
//        initializeWeather(leDataTask);

//        initializeZoomAndPan();

//        layerViewport.createLayerControls(LAYER_VIEW_NAME, layerSelector);
//        layerViewport.renderLayers();
    }

//    private Task<LeData> tryLoadingData()
//    {
//        try {
//            final File file = trySelectFile();
//            return loadDataFromFile(file);
//        } catch (OperationNotSupportedException ex) {
//            //TODO handle this better in the UI!
//            Platform.exit();
//            throw new IllegalStateException("You fucked up boi");
//        }
//    }

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

    private Task<LeData> loadDataFromFile(final File file)
    {
        return executeLoadingData(createLoadingDataTaskFromFile(file));
    }

    private Task<LeData> createLoadingDataTaskFromFile(final File serFile)
    {
        final SerFileProcessor serFileProcessor = new SerFileProcessor(serFile);
        return serFileProcessor.createProcessingTask();
    }

    private Task<LeData> executeLoadingData(final Task<LeData> dataTask)
    {
        executorService.execute(dataTask);
//        executorService.execute(avalancheRiskController.prepareAvalanchePredictionTask());
        bindUi(dataTask);
        return dataTask;
    }

    private void bindUi(final Task<LeData> dataTask)
    {
        TERRAIN_ALTITUDE_LAYER.dataProperty().bind(dataTask.valueProperty());
        GRADE_LAYER.dataProperty().bind(dataTask.valueProperty());
        CURVATURE_LAYER.dataProperty().bind(dataTask.valueProperty());
    }


    private void initializeWeather(final Task<LeData> dataTask)
    {
        avalancheRiskController.prepareAvalanchePredictionTask(
                dataTask.getValue(),
                AVALANCHE_RISK_LAYER,
                HILL_SHADE_LAYER);

        LocalDate now = LocalDate.now();
        LocalDate wago = now.minus(1, ChronoUnit.WEEKS);
        fromDate.setValue(wago);
        toDate.setValue(now);

        WeatherConnector con = WeatherConnector.getInstance();

        EventStreams.changesOf(fromDate.valueProperty())
                .subscribe(val -> con.buildData(val.getNewValue(), toDate.getValue()));
        EventStreams.changesOf(toDate.valueProperty())
                .subscribe(val -> con.buildData(fromDate.getValue(), val.getNewValue()));

        tableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    WeatherDto weatherDto = new WeatherDto.Builder().build((ObservableList<String>) newValue);
                    avalancheRiskController.executeTask(weatherDto, executorService);
                });
        con.setTableView(tableView);
    }

    private void initializeZoomAndPan()
    {
        EventStreams.eventsOf(layerViewport, ScrollEvent.SCROLL)
                .map(sE -> sE.getDeltaY() / 1000)
                .accumulate(layerViewport.getZoom(), (a, b) -> Utils.clamp(1 / 16, a + b, 2.0))
                .feedTo(layerViewport.zoomProperty());

        StateMachine.init(Tuples.t(layerViewport.getPan(), Point2D.ZERO))
                .on(EventStreams.eventsOf(layerViewport, MouseEvent.MOUSE_PRESSED))
                .transition((p, m) -> Tuples.t(p._1, new Point2D(m.getX(), m.getY())))
                .on(EventStreams.eventsOf(layerViewport, MouseEvent.MOUSE_DRAGGED))
                .emit((p, m) -> Optional.of(p._1.add(p._2.subtract(m.getX(), m.getY()))))
                .on(EventStreams.eventsOf(layerViewport, MouseEvent.MOUSE_RELEASED))
                .transition((p, m) -> Tuples.t(p._1.add(p._2.subtract(m.getX(), m.getY())), Point2D.ZERO))
                .on(EventStreams.changesOf(layerViewport.zoomProperty()))
                .transmit((p, c) -> {
                    final double nz = c.getNewValue().doubleValue(), oz = c.getOldValue().doubleValue();
                    final Point2D newPan = p._1.multiply(nz / oz);
                    return Tuples.t(Tuples.t(newPan, p._2), Optional.of(newPan));
                })
                .on(EventStreams.eventsOf(centerView, MouseEvent.MOUSE_CLICKED)) // Reset view
                .transmit((p, c) -> Tuples.t(Tuples.t(Point2D.ZERO, Point2D.ZERO), Optional.of(Point2D.ZERO)))
                .toEventStream().feedTo(layerViewport.panProperty());
    }

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
