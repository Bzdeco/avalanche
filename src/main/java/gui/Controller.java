package gui;

import backend.rasterizer.HillshadeGridTask;
import backend.rasterizer.LasRasterizer;
import backend.rasterizer.LasTinTask;
import backend.rasterizer.TerrainGridTask;
import com.sun.javafx.util.Utils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

import gui.layers.*;
import org.reactfx.EventStreams;
import org.reactfx.StateMachine;
import org.reactfx.util.Tuple2;
import org.reactfx.util.Tuples;
import tinfour.virtual.VirtualIncrementalTin;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class Controller {
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
    public void initialize() {
        registerLayers();

        TextAreaAppender.setTextArea(logTextArea);

        EventStreams.eventsOf(vp, ScrollEvent.SCROLL)
                    .map(sE -> sE.getDeltaY() / 1000)
                    .accumulate(vp.getZoom(), (a, b) -> Utils.clamp(1/16, a + b, 2.0))
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

            TreeItem<String> colorSelect = new TreeItem<>("Kolor");
            ColorPicker picker = new ColorPicker();
            picker.setValue(l.getColor());
            colorSelect.setGraphic(picker);

            TreeItem<String> alphaSlider = new TreeItem<>("Alpha");
            Slider slider = new Slider(0.1, 1.0, 0.80);
            alphaSlider.setGraphic(slider);

            slider.valueProperty().bindBidirectional(layer._2.opacityProperty());
            picker.valueProperty().bindBidirectional(layer._1.colorProperty());

            layerItem.getChildren().addAll(colorSelect, alphaSlider);
            layersRoot.getChildren().add(layerItem);
        }

        layerSelector.setRoot(layersRoot);
    }

    private void registerLayers() {
        GridLayer terrain = new GridLayer("Teren", Color.GREEN);
        GridLayer hillshade = new GridLayer("Zacienienie", Color.YELLOW);

        File lasfile = new File(getClass().getClassLoader().getResource("sample.las").getFile());

        LasTinTask makeTin = new LasTinTask(lasfile);

        Thread t1 = new Thread(makeTin);
        t1.setDaemon(true);
        t1.start();

        progress.progressProperty().bind(makeTin.progressProperty());

        makeTin.setOnSucceeded(ev -> {
            VirtualIncrementalTin tin = (VirtualIncrementalTin)ev.getSource().getValue();
            TerrainGridTask makeTerrainGrid = new TerrainGridTask(tin, makeTin.getGrid());

            Thread t2 = new Thread(makeTerrainGrid);
            t2.setDaemon(true);
            t2.start();

            terrain.dataProperty().bind(makeTerrainGrid.valueProperty());

            HillshadeGridTask makeHillshadeGrid = new HillshadeGridTask(tin, makeTin.getGrid(), 0.25);

            Thread t3 = new Thread(makeHillshadeGrid);
            t3.setDaemon(true);
            t3.start();

            hillshade.dataProperty().bind(makeHillshadeGrid.valueProperty());
        });


        terrain.setVisible(true);
        hillshade.setVisible(true);
        vp.registerLayer(hillshade);
        vp.registerLayer(terrain);
        vp.registerLayer(new BackgroundLayer("Tło", Color.BLACK));

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
