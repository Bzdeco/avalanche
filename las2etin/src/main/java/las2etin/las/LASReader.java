package las2etin.las;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tinfour.common.Vertex;
import tinfour.las.LasFileReader;
import tinfour.las.LasPoint;
import tinfour.test.utils.VertexWithClassification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LASReader
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LASReader.class);

    private final LASFile file;
    private final LASReaderOptions options;
    private final LasPoint pointHolder;
    private final LasFileReader tinfourReader;

    private List<Vertex> vertices;

    private LASReader(LASFile file, LASReaderOptions options, LasPoint pointHolder, LasFileReader tinfourReader)
    {
        this.file = file;
        this.options = options;
        this.pointHolder = pointHolder;
        this.tinfourReader = tinfourReader;
    }

    public static LASReader createFor(LASFile file)
    {
        return createFor(file, LASReaderOptions.getDefault());
    }

    public static LASReader createFor(LASFile file, LASReaderOptions options)
    {
        try {
            return initializeReader(file, options);
        }
        catch (IOException ex) {
            return handleReaderInitializationError(ex);
        }
    }

    private static LASReader initializeReader(LASFile file, LASReaderOptions options) throws IOException
    {
        LasPoint pointHolder = new LasPoint();
        LasFileReader tinfourReader = new LasFileReader(file.getPath().toFile());
        return new LASReader(file, options, pointHolder, tinfourReader);
    }

    private static LASReader handleReaderInitializationError(IOException ex)
    {
        String errorMessage = String.format("Failed to initialize %s", LasFileReader.class.getCanonicalName());
        LOGGER.error(errorMessage, ex);
        throw new IllegalStateException(errorMessage, ex);
    }

    public List<Vertex> getVerticesRecords() throws IOException
    {
        getAllVerticesFromFile();
        filterByThinningFactor();
        trimIfNumberOfVerticesExceedsMaxNumber();

        return vertices;
    }

    private void getAllVerticesFromFile()
    {
        long numberOfPoints = tinfourReader.getNumberOfPointRecords();

        vertices = new ArrayList<>();
        for (long pointIndex = 0; pointIndex < numberOfPoints; pointIndex++) {
            convertPointIntoVertex(pointIndex).ifPresent(vertices::add);
        }
    }

    private void filterByThinningFactor()
    {
        if (options.isThinningEnabled()) {
            VertexFilter thinningFilter = new ThinningVertexFilter(options.getVerticesNumberThinningFactor());
            vertices = vertices.stream().filter(thinningFilter::accept).collect(Collectors.toList());
        }
    }

    private void trimIfNumberOfVerticesExceedsMaxNumber()
    {
        if (vertices.size() > options.getMaxNumberOfVertices()) {
            vertices = vertices.stream().limit(options.getMaxNumberOfVertices()).collect(Collectors.toList());
        }
    }

    private Optional<Vertex> convertPointIntoVertex(long pointIndex)
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