package backend.rasterizer;

import javafx.concurrent.Task;
import tinfour.common.IMonitorWithCancellation;

public abstract class TaskWithMonitor<V> extends Task<V> {
    protected IMonitorWithCancellation mon = new IMonitorWithCancellation() {
        @Override
        public int getReportingIntervalInPercent() { return 5; }

        @Override
        public void reportProgress(int progressValueInPercent) {
            updateProgress(progressValueInPercent, 100);
        }

        @Override
        public void reportDone() { }

        @Override
        public void postMessage(String message) {
            updateMessage(message);
        }

        @Override
        public boolean isCanceled() {
            return TaskWithMonitor.this.isCancelled();
        }
    };
}
