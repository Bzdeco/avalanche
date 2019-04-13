package las2etin.display;

import las2etin.model.Terrain;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Log4j2
public class TerrainFormatter
{
    public static Path serialize(Terrain terrain, Path destinationFilePath)
    {
        try {
            return serializeTerrain(terrain, destinationFilePath);
        }
        catch (IOException ex) {
            handleWritingToFileError(ex, destinationFilePath.toAbsolutePath().toString());
            return null;
        }
    }

    private static Path serializeTerrain(Terrain terrain, Path destinationFilePath) throws IOException
    {
        byte[] bytesOfSerializedTerrain = SerializationUtils.serialize(terrain);
        return Files.write(destinationFilePath, bytesOfSerializedTerrain);
    }

    private static void handleWritingToFileError(Exception cause, String filePath)
    {
        String errorMessage = String.format("Failed to save serialized terrain to %s", filePath);
        log.error(errorMessage);
        throw new IllegalStateException(errorMessage, cause);
    }

    public static Terrain deserialize(Path sourceFilePath)
    {
        try {
            return deserializeTerrain(sourceFilePath);
        }
        catch (IOException ex) {
            handleReadingFromFileError(ex, sourceFilePath.toAbsolutePath().toString());
            return null;
        }
    }

    private static Terrain deserializeTerrain(Path sourceFilePath) throws IOException
    {
        byte[] readBytesOfSerializedTerrain = Files.readAllBytes(sourceFilePath);
        return SerializationUtils.deserialize(readBytesOfSerializedTerrain);
    }

    private static void handleReadingFromFileError(Exception cause, String filePath)
    {
        String errorMessage = String.format("Failed to read serialized terrain from %s", filePath);
        log.error(errorMessage);
        throw new IllegalStateException(errorMessage, cause);
    }
}
