package las2etin.las;

public class LASReaderOptionsBuilder
{
    private int maxNumberOfVertices = Integer.MAX_VALUE;
    private boolean thinningEnabled = false;
    private float thinningFactor = 1f;

    public LASReaderOptionsBuilder()
    {
    }

    public int getMaxNumberOfVertices()
    {
        return maxNumberOfVertices;
    }

    public boolean isThinningEnabled()
    {
        return thinningEnabled;
    }

    public float getThinningFactor()
    {
        return thinningFactor;
    }

    public LASReaderOptionsBuilder withMaxNumberOfVertices(int numberOfVerticies)
    {
        maxNumberOfVertices = numberOfVerticies;
        return this;
    }

    public LASReaderOptionsBuilder withThinningEnabled(boolean enabled)
    {
        thinningEnabled = enabled;
        return this;
    }

    public LASReaderOptionsBuilder withThinningFactor(float factor)
    {
        thinningFactor = factor;
        return this;
    }


    public LASReaderOptions build()
    {
        return new LASReaderOptions(this);
    }

}
