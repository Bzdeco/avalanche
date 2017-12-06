package backend;

import javafx.concurrent.Task;

public interface FileProcessor
{
    Task<LeData> createProcessingTask();
}
