package las2etin.las;

import las2etin.model.Bounds;
import las2etin.las.vertex.ThinningVertexFilter;
import las2etin.las.vertex.VertexUtil;
import tinfour.common.Vertex;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LASReader
{
    private final LASFile file;
    private final LASReaderOptions options;
    private final VertexLoader loader;

    LASReader(LASFile file, LASReaderOptions options, VertexLoader loader)
    {
        this.file = file;
        this.options = options;
        this.loader = loader;
    }

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