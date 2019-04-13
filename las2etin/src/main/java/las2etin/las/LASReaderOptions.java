package las2etin.las;

import lombok.Getter;

@Getter
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
}
