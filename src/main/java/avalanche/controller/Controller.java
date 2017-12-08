package avalanche.controller;

import avalanche.model.LeData;
import avalanche.model.fileprocessors.SerFileProcessor;
import avalanche.view.layers.AvalancheRiskLayer;
import avalanche.view.layers.CurvatureLayer;
import avalanche.view.layers.GradeLayer;
import avalanche.view.layers.HillShadeLayer;
import avalanche.view.layers.LayerViewport;
import avalanche.view.layers.TerrainAltitudeLayer;
import backend.rasterizer.tasks.AvalancheRisk;
import backend.service.WeatherConnector;
import com.google.common.collect.ImmutableList;
import com.sun.javafx.util.Utils;
import dto.WeatherDto;
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
    private static final TerrainAltitudeLayer TERRAIN_ALTITUDE_LAYER = new TerrainAltitudeLayer("Teren");
    private static final AvalancheRiskLayer AVALANCHE_RISK_LAYER = new AvalancheRiskLayer("Ryzyko lawinowe");
    private static final String LAYER_VIEW_NAME = "Warstwy";

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

    private ExecutorService executorService = Executors.newFixedThreadPool(6);
    private AvalancheRisk calculateRisk;

    @FXML
    public void initialize()
    {
        layerViewport.registerLayers(ImmutableList.of(
                CURVATURE_LAYER,
                GRADE_LAYER,
                HILL_SHADE_LAYER,
                TERRAIN_ALTITUDE_LAYER,
                AVALANCHE_RISK_LAYER
        ));

        try {
            final File file = trySelectingFile();
            loadDataFromFile(file);
        } catch (OperationNotSupportedException ex) {
            //TODO handle this better in the UI!
            Platform.exit();
        }

        initializeWeather();
        initializeZoomAndPan();

        layerViewport.createLayerControls(LAYER_VIEW_NAME, layerSelector);
        layerViewport.renderLayers();
    }

    private File trySelectingFile() throws OperationNotSupportedException
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

    private void loadDataFromFile(final File file)
    {
        executeLoadingData(createTaskLoadingFromSerFile(file));
    }

    private Task<LeData> createTaskLoadingFromSerFile(final File serFile)
    {
        final SerFileProcessor serFileProcessor = new SerFileProcessor(serFile);
        return serFileProcessor.createProcessingTask();
    }

    private void executeLoadingData(final Task<LeData> dataTask)
    {
        executorService.execute(dataTask);
        calculateRisk = new AvalancheRisk(dataTask); // TODO check if this can be converted to local
//        calculateRisk.setExecutor(executorService); TODO check if this is needed when app works

        bindUi(dataTask);
    }

    private void bindUi(final Task<LeData> dataTask)
    {
        TERRAIN_ALTITUDE_LAYER.dataProperty().bind(dataTask.valueProperty());
        GRADE_LAYER.dataProperty().bind(dataTask.valueProperty());
        CURVATURE_LAYER.dataProperty().bind(dataTask.valueProperty());
        AVALANCHE_RISK_LAYER.dataProperty().bind(calculateRisk.valueProperty());
        HILL_SHADE_LAYER.dataProperty().bind(calculateRisk.valueProperty());
    }


    private void initializeWeather()
    {

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
                    WeatherDto weatherDto = new WeatherDto.Builder()
                    .build((ObservableList<String>) newValue);
                    calculateRisk.setWeather(weatherDto);
                    calculateRisk.restart();
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
