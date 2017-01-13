package gui;

import backend.AvalancheModel;
import backend.ResourceHandler;
import backend.rasterizer.*;
import backend.service.WeatherAnimateTask;
import backend.service.WeatherConnector;
import com.sun.javafx.util.Utils;
import gui.layers.BackgroundLayer;
import gui.layers.ColorRamp;
import gui.layers.GridLayer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reactfx.EventStreams;
import org.reactfx.StateMachine;
import org.reactfx.util.Tuple2;
import org.reactfx.util.Tuples;
import tinfour.testutils.GridSpecification;

import java.io.File;
import java.util.Optional;

public class Controller {
    private static final Logger logger = LogManager.getLogger();

    @FXML
    public Button centerView;

    @FXML
    private ProgressBar progress;

    @FXML
    private TreeView layerSelector;

    @FXML
    private TextArea logTextArea;

    @FXML
    private Viewport vp;

    @FXML
    private DatePicker fromDate;

    @FXML
    private DatePicker toDate;

    @FXML
    private Button playBtn;

    @FXML
    private Button submitBtn;

    @FXML
    private TableView tableView;

    private AvalancheModel avalancheModel = new AvalancheModel();

    @FXML
    public void initialize() {
        registerLayers();

        submitBtn.setOnAction(event -> {
            WeatherConnector con = new WeatherConnector(tableView);
            con.buildData(fromDate.getValue(), toDate.getValue());
        });

        playBtn.setOnAction(event -> {
            int start = tableView.getSelectionModel().getSelectedIndex();
            if (start == -1)
                start = 0;
            ObservableList<ObservableList<String>> list = (ObservableList<ObservableList<String>>) tableView.getItems();

            WeatherAnimateTask weatherAnimateTask = new WeatherAnimateTask(start, list, avalancheModel);

            Thread t1 = new Thread(weatherAnimateTask);
            t1.start();

        });


        tableView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue)
                        -> {
                    avalancheModel.setCurrentWeather((ObservableList<String>) newValue);
                    logger.info("Current weather set to: \n" + avalancheModel.getCurrentWeather().toString());
                });


        EventStreams.eventsOf(vp, ScrollEvent.SCROLL)
                .map(sE -> sE.getDeltaY() / 1000)
                .accumulate(vp.getZoom(), (a, b) -> Utils.clamp(1 / 16, a + b, 2.0))
                .feedTo(vp.zoomProperty());

        // Bind pan
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

        createLayerControls();

        vp.enableRendering();
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
            Slider slider = new Slider(0.1, 1.0, 0.80);
            alphaSlider.setGraphic(slider);

            slider.valueProperty().bindBidirectional(layer._2.opacityProperty());

            layerItem.getChildren().add(alphaSlider);
            layersRoot.getChildren().add(layerItem);
        }

        layerSelector.setRoot(layersRoot);
    }

    private void registerLayers() {
        GridLayer terrain = new GridLayer("Teren", ColorRamp.create()
                .step(4000, 255, 255, 255, 255)
                .step(2800, 110, 110, 110, 255)
                .step(1700, 158, 0, 0, 255)
                .step(1200, 161, 67, 0, 255)
                .step(500, 232, 215, 125, 255)
                .step(50, 16, 122, 47, 255)
                .step(0, 0, 97, 71, 255)
                .build());

        GridLayer hillshade = new GridLayer("Zacienienie", ColorRamp.create()
                .step(1, 255, 255, 255, 127)
                .step(0, 0, 0, 0, 0)
                .build());

        GridLayer steepness = new GridLayer("Nachylenie terenu", ColorRamp.create()
                .step(-(float) Math.PI, 0, 0, 255, 255)
                .step((float) Math.PI, 255, 0, 0, 255)
                .build());

        GridLayer curvature = new GridLayer("Krzywizna terenu", ColorRamp.create()
                .step(-1, 0, 0, 255, 255)
                .step(0, 0, 255, 0, 255)
                .step(1, 255, 0, 0, 255)
                .build());

        File lasfile = new File(ResourceHandler.getMainDataFilePath());

        LasTinTask makeTin = new LasTinTask(lasfile);
        progress.progressProperty().bind(makeTin.progressProperty());
        makeTin.rnext(tin -> {
            GridSpecification grid = makeTin.getGrid();
            (new CachedTask<>(ResourceHandler.getTerrainDataFilePath(), new TerrainGridTask(tin, grid))).rnext(terrain.dataProperty(), dem -> {
                (new CurvatureGridTask(dem)).rnext(curvature.dataProperty());
            });
            (new CachedTask<>(ResourceHandler.getNormalsFilePath(), new NormalsTask(tin, grid))).rnext(norm -> {
                (new CachedTask<>(ResourceHandler.getHillShadeDataFilePath(), new HillshadeGridTask(norm, 0.25f))).rnext(hillshade.dataProperty());
                (new CachedTask<>(ResourceHandler.getSteepnessDataFilePath(), new SteepnessGridTask(norm))).rnext(steepness.dataProperty());
            });
        });

        terrain.setVisible(true);
        hillshade.setVisible(true);

        vp.registerLayer(curvature);
        vp.registerLayer(steepness);
        vp.registerLayer(hillshade);
        vp.registerLayer(terrain);
        vp.registerLayer(new BackgroundLayer("Tło"));

//        Layer risk = new GridLayer("Ryzyko lawinowe", Color.RED);
//        risk.setVisible(true);
//        vp.registerLayer(risk);
//
//        vp.registerLayer(new GridLayer("Temperatura gruntu", Color.BLUE));
//        vp.registerLayer(new GridLayer("Grubość pokrywy śnieżnej", Color.BLUE));
//        vp.registerLayer(new VectorLayer("Prędkość wiatru", Color.BLUE));
//        vp.registerLayer(new GridLayer("Opady", Color.BLUE));
    }
}
