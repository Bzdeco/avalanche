package las2etin.las;

import las2etin.model.Bounds;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.tinfour.common.Vertex;
import org.tinfour.gis.las.LasFileReader;
import org.tinfour.gis.las.LasPoint;
import org.tinfour.gis.las.LasRecordFilterByLastReturn;
import org.tinfour.gis.utils.VertexWithClassification;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class VertexLoader
{
    private final LasPoint pointHolder;
    private final LasFileReader tinfourReader;
    private final LasRecordFilterByLastReturn lastReturnFilter;

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
        return new VertexLoader(new LasPoint(), new LasFileReader(file), new LasRecordFilterByLastReturn());
    }

    private static void handleVertexLoaderInitializationError(Exception ex)
    {
        String errorMessage = String.format("Failed to initialize %s", LasFileReader.class.getCanonicalName());
        log.error(errorMessage, ex);
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

    Bounds getRealBounds()
    {
        double minX = tinfourReader.getMinX();
        double maxX = tinfourReader.getMaxX();
        double minY = tinfourReader.getMinY();
        double maxY = tinfourReader.getMaxY();
        double minZ = tinfourReader.getMinZ();
        double maxZ = tinfourReader.getMaxZ();

        return new Bounds(minX, minY, maxX, maxY, minZ, maxZ);
    }

    private Optional<Vertex> getVertexForIndexedPoint(long pointIndex)
    {
        try {
            return readGroundPointRecord(pointIndex);
        }
        catch (IOException ex) {
            return handleUnreadablePointRecord(pointIndex);
        }
    }

    private Optional<Vertex> readGroundPointRecord(long pointIndex) throws IOException
    {
        tinfourReader.readRecord(pointIndex, pointHolder);

        if(isLastReturnPoint(pointHolder) && isRealAltitude(pointHolder.z)) {
            Vertex vertex = new VertexWithClassification(pointHolder.x,
                                                         pointHolder.y,
                                                         pointHolder.z,
                                                         (int) pointIndex,
                                                         pointHolder.classification);
            return Optional.of(vertex);
        }
        return Optional.empty();
    }

    private Optional<Vertex> handleUnreadablePointRecord(long pointIndex)
    {
        log.info(String.format("Unable to read data for LAS record (id: %d), skipping the entry", pointIndex));
        return Optional.empty();
    }

    private boolean isLastReturnPoint(LasPoint point) {
        return lastReturnFilter.accept(point);
    }

    private boolean isGroundPoint(LasPoint point)
    {
        return point.classification == 2;
    }

    private boolean isRealAltitude(double altitude)
    {
        return altitude >= 0 && altitude <= 2500;
    }
}
