package avalanche.model.fileprocessors;

import avalanche.model.LeData;
import javafx.concurrent.Task;

public interface FileProcessor
{
    Task<LeData> createProcessingTask();
}
