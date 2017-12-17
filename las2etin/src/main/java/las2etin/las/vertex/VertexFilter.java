package las2etin.las.vertex;

import tinfour.common.Vertex;

public interface VertexFilter
{
    boolean accept(Vertex candidate);
}
