package las2etin.las.vertex;

import lombok.AllArgsConstructor;
import org.tinfour.common.Vertex;

import java.util.Random;

@AllArgsConstructor
public final class ThinningVertexFilter implements VertexFilter
{
    private final float thinningFactor;

    private Random generator;

    public ThinningVertexFilter(float thinningFactor)
    {
        this(thinningFactor, new Random());
    }

    @Override
    public boolean accept(Vertex candidate)
    {
        return generator.nextFloat() <= thinningFactor;
    }
}
