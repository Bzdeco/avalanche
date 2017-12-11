package las2etin.las;

import tinfour.common.Vertex;

public interface VertexFilter
{
    boolean accept(Vertex candidate);
}
