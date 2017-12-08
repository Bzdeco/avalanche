package avalanche.controller;

import avalanche.model.LeData;
import avalanche.model.fileprocessors.SerFileProcessor;
import avalanche.view.Viewport;
import avalanche.view.layers.AvalancheRiskLayer;
import avalanche.view.layers.CurvatureLayer;
import avalanche.view.layers.GradeLayer;
import avalanche.view.layers.HillShadeLayer;
import avalanche.view.layers.LayerView;
import avalanche.view.layers.TerrainAltitudeLayer;
import avalanche.view.layers.renderers.GridLayerRenderer;
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
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reactfx.EventStreams;
import org.reactfx.StateMachine;
import org.reactfx.util.Tuple2;
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
    private static final String CURVATURE_NAME = "Krzywizna terenu";
    private static final String GRADE_NAME = "Nachylenie terenu";
    private static final String HILL_SHADE_NAME = "Zacienienie";
    private static final String TERRAIN_ALTITUDE_NAME = "Teren";
    private static final String AVALANCHE_RISK_NAME = "Ryzyko lawinowe";
    private static final CurvatureLayer CURVATURE_LAYER = new CurvatureLayer(CURVATURE_NAME);
    private static final GradeLayer GRADE_LAYER = new GradeLayer(GRADE_NAME);
    private static final HillShadeLayer HILL_SHADE_LAYER = new HillShadeLayer(HILL_SHADE_NAME);
    private static final TerrainAltitudeLayer TERRAIN_ALTITUDE_LAYER = new TerrainAltitudeLayer(TERRAIN_ALTITUDE_NAME);
    private static final AvalancheRiskLayer AVALANCHE_RISK_LAYER = new AvalancheRiskLayer(AVALANCHE_RISK_NAME);

    @FXML
    public Button centerView;

    @FXML
    private ProgressBar progress;

    @FXML
    private TreeView layerSelector;

    @FXML
    private Viewport viewport;

    @FXML
    private DatePicker fromDate;

    @FXML
    private DatePicker toDate;

    @FXML
    private Button playBtn;

    @FXML
    private TableView tableView;

    private ExecutorService executorService = Executors.newFixedThreadPool(6);
    private LayerController layerController;

    private AvalancheRisk calculateRisk;
    private GridLayerRenderer terrain;
    private GridLayerRenderer curvature;
    private GridLayerRenderer grade;
    private GridLayerRenderer hillshade;
    private GridLayerRenderer risk;

    @FXML
    public void initialize()
    {
        layerController = LayerController.initializeWithLayers(
                ImmutableList.of(
                        CURVATURE_LAYER,
                        GRADE_LAYER,
                        HILL_SHADE_LAYER,
                        TERRAIN_ALTITUDE_LAYER,
                        AVALANCHE_RISK_LAYER),
                viewport
        );

        try {
            final File file = trySelectingFile();
            loadDataFromFile(file);
        } catch (OperationNotSupportedException ex) {
            //TODO handle this better in the UI!
            Platform.exit();
        }

        initWeather();
        initZoomAndPan();
        createLayerControls();
        layerController.renderLayers();
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

    private void executeLoadingData(final Task<LeData> data)
    {
        executorService.execute(data);
        //TODO can this ui binding be moved?
        TERRAIN_ALTITUDE_LAYER.dataProperty().bind(data.valueProperty());
        GRADE_LAYER.dataProperty().bind(data.valueProperty());
        CURVATURE_LAYER.dataProperty().bind(data.valueProperty());

        calculateRisk = new AvalancheRisk(data);
        calculateRisk.setExecutor(executorService);
        AVALANCHE_RISK_LAYER.dataProperty().bind(calculateRisk.valueProperty());
        HILL_SHADE_LAYER.dataProperty().bind(calculateRisk.valueProperty());
    }

    private void createLayerControls()
    {
        TreeItem<String> layersRoot = new TreeItem<>("Warstwy");
        layersRoot.setExpanded(true);

        for (Tuple2<LayerView, Canvas> layer : layerController.getLayers()) {
            LayerView l = layer._1;

            CheckBox layerToggle = new CheckBox();
            layerToggle.selectedProperty().bindBidirectional(l.isVisibleProperty());
            ProgressIndicator layerLoadIndicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
            layerLoadIndicator.setPrefWidth(16);
            layerLoadIndicator.setPrefHeight(16);

            EventStreams.valuesOf(l.readyProperty()) // was setting to true in constructor |ReadOnlyBooleanWrapper
                    .map(r -> r ? null : layerLoadIndicator)
                    .feedTo(layerToggle.graphicProperty());

            TreeItem<String> layerItem = new TreeItem<>();
            layerItem.valueProperty().bindBidirectional(l.nameProperty());
            layerItem.setGraphic(layerToggle);

            TreeItem<String> alphaSlider = new TreeItem<>("Alpha");
            Slider slider = new Slider(0.1, 1.0, 0.50);
            alphaSlider.setGraphic(slider);

            slider.valueProperty().bindBidirectional(layer._2.opacityProperty());

            layerItem.getChildren().add(alphaSlider);
            layersRoot.getChildren().add(layerItem);
        }

        //noinspection unchecked
        layerSelector.setRoot(layersRoot);
    }

    private void initWeather()
    {
        WeatherConnector con = WeatherConnector.getInstance();
        con.setTableView(tableView);
        LocalDate now = LocalDate.now(), wago = now.minus(1, ChronoUnit.WEEKS);

        EventStreams.changesOf(fromDate.valueProperty()).subscribe(val -> con.buildData(val.getNewValue(), toDate.getValue()));

        EventStreams.changesOf(toDate.valueProperty()).subscribe(val -> con.buildData(fromDate.getValue(), val.getNewValue()));

        fromDate.setValue(wago);
        toDate.setValue(now);

        //noinspection unchecked
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            @SuppressWarnings("unchecked") WeatherDto w = new WeatherDto.Builder().build((ObservableList<String>) newValue);
            calculateRisk.setWeather(w);
            calculateRisk.restart();
        });
    }

    private void initZoomAndPan()
    {
        EventStreams.eventsOf(viewport, ScrollEvent.SCROLL)
                .map(sE -> sE.getDeltaY() / 1000)
                .accumulate(viewport.getZoom(), (a, b) -> Utils.clamp(1 / 16, a + b, 2.0))
                .feedTo(viewport.zoomProperty());

        StateMachine.init(Tuples.t(viewport.getPan(), Point2D.ZERO))
                .on(EventStreams.eventsOf(viewport, MouseEvent.MOUSE_PRESSED))
                .transition((p, m) -> Tuples.t(p._1, new Point2D(m.getX(), m.getY())))
                .on(EventStreams.eventsOf(viewport, MouseEvent.MOUSE_DRAGGED))
                .emit((p, m) -> Optional.of(p._1.add(p._2.subtract(m.getX(), m.getY()))))
                .on(EventStreams.eventsOf(viewport, MouseEvent.MOUSE_RELEASED))
                .transition((p, m) -> Tuples.t(p._1.add(p._2.subtract(m.getX(), m.getY())), Point2D.ZERO))
                .on(EventStreams.changesOf(viewport.zoomProperty()))
                .transmit((p, c) -> {
                    final double nz = c.getNewValue().doubleValue(), oz = c.getOldValue().doubleValue();
                    final Point2D newPan = p._1.multiply(nz / oz);
                    return Tuples.t(Tuples.t(newPan, p._2), Optional.of(newPan));
                })
                .on(EventStreams.eventsOf(centerView, MouseEvent.MOUSE_CLICKED)) // Reset view
                .transmit((p, c) -> Tuples.t(Tuples.t(Point2D.ZERO, Point2D.ZERO), Optional.of(Point2D.ZERO)))
                .toEventStream().feedTo(viewport.panProperty());
    }

    public void deinitialize() throws InterruptedException
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
