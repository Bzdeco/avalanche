package backend.rasterizer;


import backend.serializers.DataSerializer;

public class CachedTask<T> extends ChainTask<T> {
    private DataSerializer<T> dataSerializer;
    private ChainTask<T> innerTask;
    private T cache;

    public CachedTask(String filename, ChainTask<T> innerTask) {
        this.innerTask = innerTask;
        dataSerializer = new DataSerializer<>(filename);
    }

    public T call() throws Exception {
        if (dataSerializer.isSerializedDataExistence()) {
            cache = dataSerializer.deserialize();
        } else {
            cache = innerTask.call();
            dataSerializer.serialize(cache);
        }

        return cache;
    }
}
