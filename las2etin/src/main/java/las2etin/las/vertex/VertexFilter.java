package las2etin.las.vertex;

import org.tinfour.common.Vertex;

public interface VertexFilter
{
    boolean accept(Vertex candidate);
}
