package las2etin.las;

public class LASReaderOptions
{
    private final int maxNumberOfVertices;
    private final boolean thinningEnabled;
    private final float thinningFactor;

    LASReaderOptions(LASReaderOptionsBuilder builder)
    {
        this.maxNumberOfVertices = builder.getMaxNumberOfVertices();
        this.thinningEnabled = builder.isThinningEnabled();
        this.thinningFactor = builder.getThinningFactor();
    }

    public static LASReaderOptions getDefault()
    {
        return new LASReaderOptionsBuilder().build();
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
}
