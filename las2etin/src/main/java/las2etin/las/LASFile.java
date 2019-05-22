package las2etin.las;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class LASFile
{
    private final Path path;

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
