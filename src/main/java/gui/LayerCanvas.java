package gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

public class LayerCanvas extends Pane {
    private final Canvas canvas = new Canvas();

    public LayerCanvas() {
        getChildren().add(canvas);
    }

    @Override
    protected void layoutChildren() {
        final int top = (int)snappedTopInset();
        final int right = (int)snappedRightInset();
        final int bottom = (int)snappedBottomInset();
        final int left = (int)snappedLeftInset();
        final int w = (int)getWidth() - left - right;
        final int h = (int)getHeight() - top - bottom;
        canvas.setLayoutX(left);
        canvas.setLayoutY(top);
        canvas.setWidth(w);
        canvas.setHeight(h);
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
