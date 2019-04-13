package las2etin.las;

import las2etin.las.vertex.ThinningVertexFilter;
import las2etin.las.vertex.VertexUtil;
import las2etin.model.Bounds;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.tinfour.common.Vertex;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class LASReader
{
    private final LASFile file;
    private final LASReaderOptions options;
    private final VertexLoader loader;

    public static LASReader createFor(LASFile file)
    {
        return createFor(file, LASReaderOptions.getDefault());
    }

    public static LASReader createFor(LASFile file, LASReaderOptions options)
    {
        File plainFileHandle = file.getPath().toFile();
        VertexLoader loader = VertexLoader.create(plainFileHandle);
        return new LASReader(file, options, loader);
    }

    public List<Vertex> getVerticesRecords() throws IOException
    {
        List<Vertex> vertices = loader.getAllVerticesFromFile();
        if (options.isThinningEnabled())
        {
            ThinningVertexFilter filter = new ThinningVertexFilter(options.getThinningFactor());
            vertices = VertexUtil.filter(vertices, filter);
        }
        vertices = VertexUtil.trimNumberOfVertices(vertices, options.getMaxNumberOfVertices());

        return vertices;
    }

    public Bounds getVerticesBounds()
    {
        return loader.getRealBounds();
    }
}