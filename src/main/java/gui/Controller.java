package gui;

import com.sun.javafx.util.Utils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import layers.*;
import log.TextAreaAppender;
import org.reactfx.EventStreams;
import org.reactfx.StateMachine;
import org.reactfx.util.Tuples;

import java.util.Optional;

public class Controller {
    @FXML
    public Button tmpBtn;

    @FXML
    // canvasWrapper is just nasty fix for auto-resizing canvas
    private LayerCanvas canvasWrapper;
    private Canvas canvas;

    @FXML
    private TreeView layerSelector;

    @FXML
    private TextArea logTextArea;

    private Viewport vp;

    @FXML
    public void initialize() {
        canvas = canvasWrapper.getCanvas();
        vp = new Viewport(canvas.getGraphicsContext2D());

        registerLayers();

        TextAreaAppender.setTextArea(logTextArea);

        // Bind width and height
        vp.heightProperty().bind(canvas.heightProperty());
        vp.widthProperty().bind(canvas.widthProperty());

        // TODO refactor into streams
        canvas.setOnScroll(scrollEvent -> {
            double newZoom = vp.getZoom() + scrollEvent.getDeltaY() / 1000;
            newZoom = Utils.clamp(0.1, newZoom, 2.0);
            vp.setZoom(newZoom);
        });

        // Bind pan
        StateMachine.init(Tuples.t(vp.getPan(), Point2D.ZERO))
                .on(EventStreams.eventsOf(canvas, MouseEvent.MOUSE_PRESSED))
                    .transition((p, m) -> Tuples.t(p._1, new Point2D(m.getX(), m.getY())))
                .on(EventStreams.eventsOf(canvas, MouseEvent.MOUSE_DRAGGED))
                    .emit((p, m) -> Optional.of(p._1.add(p._2.subtract(m.getX(), m.getY()))))
                .on(EventStreams.eventsOf(canvas, MouseEvent.MOUSE_RELEASED))
                    .transition((p, m) -> Tuples.t(p._1.add(p._2.subtract(m.getX(), m.getY())), Point2D.ZERO))
                .on(EventStreams.changesOf(vp.zoomProperty()))
                    .transmit((p, c) -> {
                        final double zoomChange = c.getNewValue().doubleValue() - c.getOldValue().doubleValue();
                        final Point2D newPan = p._1.multiply(zoomChange); // TODO find correct formula :)
                        return Tuples.t(Tuples.t(newPan, p._2), Optional.of(newPan));
                    })
                .toEventStream().feedTo(vp.panProperty());

        createLayerControls();

        vp.enableRendering();
    }

    private void createLayerControls() {
        TreeItem<String> layersRoot = new TreeItem<>("Warstwy");
        layersRoot.setExpanded(true);

        for (Layer l : vp.getLayers()) {
            CheckBox layerToggle = new CheckBox();
            layerToggle.selectedProperty().bindBidirectional(l.isVisibleProperty());

            TreeItem<String> layerItem = new TreeItem<>();
            layerItem.valueProperty().bindBidirectional(l.nameProperty());
            layerItem.setGraphic(layerToggle);

            TreeItem<String> colorSelect = new TreeItem<>("Kolor");
            ColorPicker picker = new ColorPicker();
            picker.setValue(l.getColor());
            colorSelect.setGraphic(picker);

            TreeItem<String> alphaSlider = new TreeItem<>("Alpha");
            Slider slider = new Slider(0.1, 1.0, 0.5);
            alphaSlider.setGraphic(slider);

            EventStreams.combine(EventStreams.valuesOf(picker.valueProperty()),
                                 EventStreams.valuesOf(slider.valueProperty()))
                        .map(t -> new Color(t._1.getRed(), t._1.getGreen(), t._1.getBlue(), (Double) t._2))
                        .feedTo(l.colorProperty());

            layerItem.getChildren().addAll(colorSelect, alphaSlider);
            layersRoot.getChildren().add(layerItem);
        }

        layerSelector.setRoot(layersRoot);
    }

    private void registerLayers() {
        Task<double[][]> createRandomData = new Task<double[][]>() {
            @Override
            protected double[][] call() throws Exception {
                Image img = new Image("lena.bmp");

                double[][] data = new double[(int)img.getHeight()][(int)img.getWidth()];

                for(int y = 0; y < data.length; ++y) {
                    for(int x = 0; x < data[0].length; ++x) {
                        data[y][x] = img.getPixelReader().getColor(x, y).getBrightness();
                    }
                }
                return data;
            }
        };

        new Thread(createRandomData).start();

        GridLayer terrain = new GridLayer("Teren", Color.GREEN);
        terrain.dataProperty().bind(createRandomData.valueProperty());
        terrain.setVisible(true);
        vp.registerLayer(terrain);

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
