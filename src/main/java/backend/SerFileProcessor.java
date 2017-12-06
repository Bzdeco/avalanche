package backend;

import backend.serializers.DataSerializer;
import javafx.concurrent.Task;

import java.io.File;

public class SerFileProcessor implements FileProcessor
{
    private DataSerializer<float[][][]> dataSerializer;

    public SerFileProcessor(final File file)
    {
        dataSerializer = new DataSerializer<>(file);
    }

    @Override
    public Task<LeData> createProcessingTask()
    {
        return new LeData(dataSerializer.deserialize());
    }
}
