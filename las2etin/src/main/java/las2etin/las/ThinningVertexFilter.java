package las2etin.las;

import tinfour.common.Vertex;

import java.util.Random;

public class ThinningVertexFilter implements VertexFilter
{
    private float thinningFactor;
    private Random generator;

    public ThinningVertexFilter(float thinningFactor)
    {
        this.thinningFactor = thinningFactor;
        this.generator = new Random();
    }

    @Override
    public boolean accept(Vertex candidate)
    {
        return generator.nextFloat() <= thinningFactor;
    }
}
