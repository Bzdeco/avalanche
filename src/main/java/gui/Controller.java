package gui;

import backend.AvalancheModel;
import backend.rasterizer.TerrainProps;
import backend.rasterizer.tasks.*;
import backend.service.WeatherAnimateTask;
import backend.service.WeatherConnector;
import com.sun.javafx.util.Utils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import gui.layers.*;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reactfx.EventStreams;
import org.reactfx.StateMachine;
import org.reactfx.util.Tuple2;
import org.reactfx.util.Tuples;

import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

import backend.ResourceHandler;

public class Controller {
    private static final Logger logger = LogManager.getLogger();

    private ExecutorService executor = Executors.newFixedThreadPool(6);

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

    private AvalancheModel avalancheModel = new AvalancheModel();

    private MultiGridLayer terrain;
    private MultiGridLayer curvature;
    private GridLayer      hillshade;
    private MultiGridLayer grade;
    private GridLayer      risk;

    @FXML
    public void initialize() {
        initWeather();
        registerLayers();
        loadFile();

        initZoomAndPan();
        createLayerControls();

        vp.enableRendering();
    }

    private void loadFile() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(Arrays.asList(
                new FileChooser.ExtensionFilter("Model lidarowy", "*.las"),
                new FileChooser.ExtensionFilter("Model zserializowany", "*.ser")
        ));

        fileChooser.setTitle("Wybierz plik modelu terenu");
        File f = fileChooser.showOpenDialog(null);

        if(f != null && f.exists()) {
            String extension = f.getName().substring(f.getName().lastIndexOf(".") + 1);

            Task<float[][][]> makeTerrain = null;
            if(extension.equals("las")) makeTerrain = loadFromLas(f);
            else if(extension.equals("ser")) makeTerrain = loadFromSer(f);

            executor.execute(makeTerrain);
            terrain.dataProperty().bind(makeTerrain.valueProperty());
            grade.dataProperty().bind(makeTerrain.valueProperty());
            curvature.dataProperty().bind(makeTerrain.valueProperty());
        } else {
            Platform.exit();
        }
    }

    Task<float[][][]> loadFromLas(File lasfile) {
        LasTin makeTin = new LasTin(lasfile);
        progress.progressProperty().bind(makeTin.progressProperty());
        executor.execute(makeTin);
        return new TinTerrain(executor, 4, makeTin);
    }

    Task<float[][][]> loadFromSer(File serfile) {
        return new SerTerrain(serfile);
    }

    private void createLayerControls() {
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

        layerSelector.setRoot(layersRoot);
    }

    private void registerLayers() {
        terrain = new MultiGridLayer("Teren", TerrainProps.ALTITUDE, ColorRamp.create()
                .step(4000, 255, 255, 255, 255)
                .step(2800, 110, 110, 110, 255)
                .step(1700, 158, 0, 0, 255)
                .step(1200, 161, 67, 0, 255)
                .step(500, 232, 215, 125, 255)
                .step(50, 16, 122, 47, 255)
                .step(0, 0, 97, 71, 255)
                .build());

        hillshade = new GridLayer("Zacienienie", ColorRamp.create()
                .step(1, 255, 255, 255, 255)
                .step(0, 0, 0, 0, 0)
                .build());

        grade = new MultiGridLayer("Nachylenie terenu", TerrainProps.GRADE, ColorRamp.create()
                .step(-(float) Math.PI, 0, 0, 255, 255)
                .step((float) Math.PI, 255, 0, 0, 255)
                .build());

        curvature = new MultiGridLayer("Krzywizna terenu", TerrainProps.PROFCURV, ColorRamp.create()
                .step(-1    ,   0,   0, 255, 255)
                .step(-0.01f,   0, 255, 255, 255)
                .step(0     ,   0, 255,   0, 255)
                .step(0.01f , 255, 255,   0, 255)
                .step(1     , 255,   0,   0, 255)
                .build());

        risk = new GridLayer("Ryzyko lawinowe", ColorRamp.create()
                .step(0,   0, 255,   0, 255)
                .step(2, 255, 255,   0, 255)
                .step(4, 255,   0,   0, 255)
                .step(5, 127,   0,  63, 255)
                .build());

        vp.registerLayer(risk);
        vp.registerLayer(curvature);
        vp.registerLayer(grade);
        vp.registerLayer(hillshade);
        vp.registerLayer(terrain);
        vp.registerLayer(new BackgroundLayer("Tło"));
    }

    private void initWeather() {
        WeatherConnector con = new WeatherConnector(tableView);
        LocalDate now = LocalDate.now(), wago = now.minus(1, ChronoUnit.WEEKS);

        EventStreams.changesOf(fromDate.valueProperty()).subscribe(val -> {
            con.buildData(val.getNewValue(), toDate.getValue());
        });

        EventStreams.changesOf(toDate.valueProperty()).subscribe(val -> {
            con.buildData(fromDate.getValue(), val.getNewValue());
        });

        fromDate.setValue(wago);
        toDate.setValue(now);

        playBtn.setOnAction(event -> {
            int start = tableView.getSelectionModel().getSelectedIndex();
            if (start == -1)
                start = 0;
            ObservableList<ObservableList<String>> list = (ObservableList<ObservableList<String>>) tableView.getItems();
            executor.submit(new WeatherAnimateTask(start, list, avalancheModel));
        });


        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            avalancheModel.setCurrentWeather((ObservableList<String>) newValue);
            logger.info("Current weather set to: \n" + avalancheModel.getCurrentWeather().toString());
        });
    }

    private void initZoomAndPan() {
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

    public void deinitialize() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        executor.shutdownNow();
    }

    public void saveTerrain(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model zserializowany", "*.ser"));

        File file = fileChooser.showSaveDialog(null);

        if(file != null){
            executor.execute(new SaveSer(file, terrain.getData()));
        }
    }
}
