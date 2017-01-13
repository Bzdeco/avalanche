package backend.rasterizer;

import javafx.beans.property.ObjectProperty;
import javafx.concurrent.Task;

import java.util.function.Consumer;

public abstract class ChainTask<U> extends Task<U> {
    private Thread th;

    public abstract U call() throws Exception;

    final public void rnext(ObjectProperty<U> where, Consumer<U> next) {
        if(where != null) where.bind(valueProperty());

        th = new Thread(this);
        th.setDaemon(true);
        th.start();

        if(next != null) {
            setOnSucceeded(ev -> {
                U res = (U) ev.getSource().getValue();
                next.accept(res);
            });
        }
    }

    final public void rnext(ObjectProperty<U> where) {
        rnext(where, null);
    }

    final public void rnext(Consumer<U> next) {
        rnext(null, next);
    }
}
