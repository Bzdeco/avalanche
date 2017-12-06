package gui;

import backend.LeData;
import backend.SerFileProcessor;
import backend.rasterizer.RiskProps;
import backend.rasterizer.TerrainProps;
import backend.rasterizer.tasks.AvalancheRisk;
import backend.rasterizer.tasks.SaveSer;
import backend.service.WeatherConnector;
import com.sun.javafx.util.Utils;
import dto.WeatherDto;
import gui.layers.BackgroundLayer;
import gui.layers.ColorRamp;
import gui.layers.MultiGridLayer;
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
    @FXML
    public Button centerView;
    @FXML
    private ProgressBar progress;
    @FXML
    private TreeView layerSelector;

    @FXML
    private Viewport vp;

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
    private MultiGridLayer terrain;
    private MultiGridLayer curvature;
    private MultiGridLayer grade;
    private MultiGridLayer hillshade;
    private MultiGridLayer risk;

    @FXML
    public void initialize()
    {
        registerLayers();

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

        vp.enableRendering();
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
        terrain.dataProperty().bind(data.valueProperty());
        grade.dataProperty().bind(data.valueProperty());
        curvature.dataProperty().bind(data.valueProperty());

        calculateRisk = new AvalancheRisk(data);
        calculateRisk.setExecutor(executorService);
        risk.dataProperty().bind(calculateRisk.valueProperty());
        hillshade.dataProperty().bind(calculateRisk.valueProperty());
    }

    private void createLayerControls()
    {
        TreeItem<String> layersRoot = new TreeItem<>("Warstwy");
        layersRoot.setExpanded(true);

        for (Tuple2<Layer, Canvas> layer : vp.getLayers()) {
            Layer l = layer._1;

            CheckBox layerToggle = new CheckBox();
            layerToggle.selectedProperty().bindBidirectional(l.isVisibleProperty());
            ProgressIndicator layerLoadIndicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
            layerLoadIndicator.setPrefWidth(16);
            layerLoadIndicator.setPrefHeight(16);

            EventStreams.valuesOf(l.isReadyProperty())
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

    private void registerLayers()
    {
        terrain = new MultiGridLayer("Teren", TerrainProps.ALTITUDE, ColorRamp.create()
                .step(4000, 255, 255, 255, 255)
                .step(2800, 110, 110, 110, 255)
                .step(1700, 158, 0, 0, 255)
                .step(1200, 161, 67, 0, 255)
                .step(500, 232, 215, 125, 255)
                .step(50, 16, 122, 47, 255)
                .step(0, 0, 97, 71, 255)
                .build());

        hillshade = new MultiGridLayer("Zacienienie", RiskProps.HILLSHADE, ColorRamp.create()
                .step(1, 255, 255, 255, 255)
                .step(0, 0, 0, 0, 0)
                .build());

        grade = new MultiGridLayer("Nachylenie terenu", TerrainProps.GRADE, ColorRamp.create()
                .step(-(float) Math.PI, 0, 0, 255, 255)
                .step((float) Math.PI, 255, 0, 0, 255)
                .build());

        curvature = new MultiGridLayer("Krzywizna terenu", TerrainProps.PROFCURV, ColorRamp.create()
                .step(-1, 0, 0, 255, 255)
                .step(-0.01f, 0, 255, 255, 255)
                .step(0, 0, 255, 0, 255)
                .step(0.01f, 255, 255, 0, 255)
                .step(1, 255, 0, 0, 255)
                .build());

        risk = new MultiGridLayer("Ryzyko lawinowe", RiskProps.RISK, ColorRamp.create()
                .step(0, 0, 255, 0, 255)
                .step(2, 255, 255, 0, 255)
                .step(4, 255, 0, 0, 255)
                .step(5, 127, 0, 63, 255)
                .build());

        vp.registerLayer(risk);
        vp.registerLayer(curvature);
        vp.registerLayer(grade);
        vp.registerLayer(hillshade);
        vp.registerLayer(terrain);
        vp.registerLayer(new BackgroundLayer("Tło"));
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
        EventStreams.eventsOf(vp, ScrollEvent.SCROLL)
                .map(sE -> sE.getDeltaY() / 1000)
                .accumulate(vp.getZoom(), (a, b) -> Utils.clamp(1 / 16, a + b, 2.0))
                .feedTo(vp.zoomProperty());

        StateMachine.init(Tuples.t(vp.getPan(), Point2D.ZERO))
                .on(EventStreams.eventsOf(vp, MouseEvent.MOUSE_PRESSED))
                .transition((p, m) -> Tuples.t(p._1, new Point2D(m.getX(), m.getY())))
                .on(EventStreams.eventsOf(vp, MouseEvent.MOUSE_DRAGGED))
                .emit((p, m) -> Optional.of(p._1.add(p._2.subtract(m.getX(), m.getY()))))
                .on(EventStreams.eventsOf(vp, MouseEvent.MOUSE_RELEASED))
                .transition((p, m) -> Tuples.t(p._1.add(p._2.subtract(m.getX(), m.getY())), Point2D.ZERO))
                .on(EventStreams.changesOf(vp.zoomProperty()))
                .transmit((p, c) -> {
                    final double nz = c.getNewValue().doubleValue(), oz = c.getOldValue().doubleValue();
                    final Point2D newPan = p._1.multiply(nz / oz);
                    return Tuples.t(Tuples.t(newPan, p._2), Optional.of(newPan));
                })
                .on(EventStreams.eventsOf(centerView, MouseEvent.MOUSE_CLICKED)) // Reset view
                .transmit((p, c) -> Tuples.t(Tuples.t(Point2D.ZERO, Point2D.ZERO), Optional.of(Point2D.ZERO)))
                .toEventStream().feedTo(vp.panProperty());
    }

    public void deinitialize() throws InterruptedException
    {
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        executorService.shutdownNow();
    }

    public void saveTerrain(MouseEvent mouseEvent)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model zserializowany", "*.ser"));

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            executorService.execute(new SaveSer(file, terrain.getData()));
        }
    }
}
