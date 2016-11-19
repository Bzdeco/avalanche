package gui;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import log.TextAreaAppender;

import java.util.Map;

public class Controller {
    @FXML
    private LayerCanvas canvas;

    @FXML
    private VBox layerSelector;

    @FXML
    private Slider zoomSlider;

    @FXML
    private TextArea logTextArea;

    private final Model model = new Model();

    private void renderMap() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        gc.clearRect(0, 0, w, h);

        for (Map.Entry<String, Layer> layerEntry : model.getLayers()) {
            Layer l = layerEntry.getValue();
            if (l.isVisible()) l.render(gc, w, h);
        }
    }

    @FXML
    public void initialize() {
        TextAreaAppender.setTextArea(logTextArea);

        Bindings.bindBidirectional(model.getZoomProperty(), zoomSlider.valueProperty());

        canvas.setOnResize(actionEvent -> { renderMap(); });

        for (Map.Entry<String, Layer> layerEntry : model.getLayers()) {
            Layer l = layerEntry.getValue();
            CheckBox layerToggler = new CheckBox();
            layerToggler.setId(layerEntry.getKey());
            layerToggler.setText(l.getName());

            Bindings.bindBidirectional(layerToggler.selectedProperty(), l.isVisibleProperty());

            layerToggler.setOnAction(actionEvent -> { renderMap(); });

            layerSelector.getChildren().add(layerToggler);
        }

        renderMap();
    }
}
