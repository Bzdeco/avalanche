package las2etin.las.vertex;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.tinfour.common.Vertex;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VertexUtil
{
    public static List<Vertex> filter(List<Vertex> vertices, VertexFilter filter)
    {
        return vertices.stream().filter(filter::accept).collect(Collectors.toList());
    }

    public static List<Vertex> trimNumberOfVertices(List<Vertex> vertices, int maxNumberOfVertices)
    {
        return vertices.stream().limit(maxNumberOfVertices).collect(Collectors.toList());
    }
}