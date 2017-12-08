package avalanche.model.fileprocessors;

import avalanche.model.LeData;
import avalanche.model.serializers.DataSerializer;
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
        return new Task<LeData>() {
            @Override
            protected LeData call() throws Exception
            {
                return process();
            }
        };
    }

    private LeData process()
    {
        return new LeData(dataSerializer.deserialize());
    }
}
