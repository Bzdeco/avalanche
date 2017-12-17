package las2etin.las;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LASFile
{
    private Path path;

    private LASFile(Path path)
    {
        this.path = path;
    }

    public Path getPath()
    {
        return path;
    }

    public static LASFile fromFilePath(String filePath) throws FileNotFoundException
    {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            return new LASFile(path);
        }
        else {
            throw new FileNotFoundException("File does not exist");
        }
    }
}
