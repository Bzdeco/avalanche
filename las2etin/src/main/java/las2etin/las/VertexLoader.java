package las2etin.las;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tinfour.common.Vertex;
import tinfour.las.LasFileReader;
import tinfour.las.LasPoint;
import tinfour.test.utils.VertexWithClassification;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class VertexLoader
{
    private static final Logger LOGGER = LoggerFactory.getLogger(VertexLoader.class);

    private final LasPoint pointHolder;
    private final LasFileReader tinfourReader;

    private VertexLoader(LasPoint pointHolder, LasFileReader tinfourReader)
    {
        this.pointHolder = pointHolder;
        this.tinfourReader = tinfourReader;
    }

    static VertexLoader create(File file)
    {
        try {
            return initializeVertexLoader(file);
        }
        catch (IOException ex) {
            handleVertexLoaderInitializationError(ex);
            return null; // suppress IntelliJ warnings
        }
    }

    private static VertexLoader initializeVertexLoader(File file) throws IOException
    {
        return new VertexLoader(new LasPoint(), new LasFileReader(file));
    }

    private static void handleVertexLoaderInitializationError(Exception ex)
    {
        String errorMessage = String.format("Failed to initialize %s", LasFileReader.class.getCanonicalName());
        LOGGER.error(errorMessage, ex);
        throw new IllegalStateException(errorMessage, ex);
    }

    List<Vertex> getAllVerticesFromFile()
    {
        long numberOfPoints = tinfourReader.getNumberOfPointRecords();

        List<Vertex> vertices = new ArrayList<>();
        for (long pointIndex = 0; pointIndex < numberOfPoints; pointIndex++) {
            getVertexForIndexedPoint(pointIndex).ifPresent(vertices::add);
        }

        return vertices;
    }

    private Optional<Vertex> getVertexForIndexedPoint(long pointIndex)
    {
        try {
            return readPointRecord(pointIndex);
        }
        catch (IOException ex) {
            return handleUnreadablePointRecord(pointIndex);
        }
    }

    private Optional<Vertex> readPointRecord(long pointIndex) throws IOException
    {
        tinfourReader.readRecord(pointIndex, pointHolder);
        Vertex vertex = new VertexWithClassification(pointHolder.x,
                                                     pointHolder.y,
                                                     pointHolder.z,
                                                     (int) pointIndex,
                                                     pointHolder.classification);
        return Optional.of(vertex);
    }

    private Optional<Vertex> handleUnreadablePointRecord(long pointIndex)
    {
        LOGGER.info(String.format("Unable to read data for LAS record (id: %d), skipping the entry", pointIndex));
        return Optional.empty();
    }
}
