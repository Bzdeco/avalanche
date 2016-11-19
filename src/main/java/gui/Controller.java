package gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

import java.util.Map;

public class Controller {
    @FXML
    public LayerCanvas canvas;

    @FXML
    public VBox layerSelector;

    @FXML
    public Slider zoomSlider;

    private Model model = new Model();

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
        zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
                // newVal.doubleValue();
            }
        });

        canvas.setOnResize(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                renderMap();
            }
        });

        for (Map.Entry<String, Layer> layerEntry : model.getLayers()) {
            Layer l = layerEntry.getValue();
            CheckBox layerToggler = new CheckBox();
            layerToggler.setId(layerEntry.getKey());
            layerToggler.setText(l.getName());
            layerToggler.setSelected(l.isVisible());

            layerToggler.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    toggleLayers(e);
                }
            });
            layerSelector.getChildren().add(layerToggler);
        }

        renderMap();
    }

    private void toggleLayers(ActionEvent actionEvent) {
        CheckBox source = (CheckBox) actionEvent.getSource();

        // For simplicity checkbox id's are directly corresponding with layer id's
        model.toggleLayer(source.getId(), source.isSelected());

        renderMap();
    }
}
