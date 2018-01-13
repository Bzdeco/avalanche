package avalanche.ser;

import avalanche.ser.model.Terrain;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TerrainFormatter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TerrainFormatter.class);

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
        LOGGER.error(errorMessage);
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
        LOGGER.error(errorMessage);
        throw new IllegalStateException(errorMessage, cause);
    }
}
