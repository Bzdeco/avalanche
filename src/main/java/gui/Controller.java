package gui;

import com.sun.javafx.util.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import layers.*;
import log.TextAreaAppender;

public class Controller {
    @FXML
    private LayerCanvas canvas;

    @FXML
    private VBox layerSelector;

    @FXML
    private Slider zoomSlider;

    @FXML
    private TextArea logTextArea;

    private Viewport vp;

    @FXML
    public void initialize() {
        vp = new Viewport(canvas.getGraphicsContext2D());

        registerLayers();

        TextAreaAppender.setTextArea(logTextArea);

        vp.zoomProperty().bindBidirectional(zoomSlider.valueProperty());

        vp.heightProperty().bind(canvas.heightProperty());
        vp.widthProperty().bind(canvas.widthProperty());

        canvas.setCanvasOnScroll(scrollEvent -> {
            double newZoom = vp.getZoom() + scrollEvent.getDeltaY() / 1000;
            newZoom = Utils.clamp(zoomSlider.getMin(), newZoom, zoomSlider.getMax());
            vp.setZoom(newZoom);
        });

        for (Layer l : vp.getLayers()) {
            CheckBox layerToggle = new CheckBox();
            layerToggle.textProperty().bindBidirectional(l.getNameProperty());
            layerToggle.selectedProperty().bindBidirectional(l.isVisibleProperty());
            layerSelector.getChildren().add(layerToggle);
        }
    }

    private void registerLayers() {
        Layer terrain = new GridLayer("Teren", Color.GREEN);
        terrain.setVisible(true);
        vp.registerLayer(terrain);

        Layer risk = new GridLayer("Ryzyko lawinowe", Color.RED);
        risk.setVisible(true);
        vp.registerLayer(risk);

        vp.registerLayer(new GridLayer("Temperatura gruntu", Color.BLUE));
        vp.registerLayer(new GridLayer("Grubość pokrywy śnieżnej", Color.BLUE));
        vp.registerLayer(new VectorLayer("Prędkość wiatru", Color.BLUE));
        vp.registerLayer(new GridLayer("Opady", Color.BLUE));
    }
}
