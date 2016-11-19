package gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

public class LayerCanvas extends Pane {
    private final Canvas canvas = new Canvas();
    private EventHandler<ActionEvent> onResize;

    public LayerCanvas() {
        getChildren().add(canvas);
    }

    public GraphicsContext getGraphicsContext2D() {
        return canvas.getGraphicsContext2D();
    }

    public void setOnResize(EventHandler<ActionEvent> onResize) {
        this.onResize = onResize;
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

        if (w != canvas.getWidth() || h != canvas.getHeight()) {
            canvas.setWidth(w);
            canvas.setHeight(h);

            onResize.handle(new ActionEvent());
        }
    }

    public void setCanvasOnScroll(EventHandler<? super ScrollEvent> scrollEvent) {
        canvas.setOnScroll(scrollEvent);
    }
}
