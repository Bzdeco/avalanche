package las2etin.las;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LASReaderOptionsBuilder
{
    private int maxNumberOfVertices = Integer.MAX_VALUE;
    private boolean thinningEnabled = false;
    private float thinningFactor = 1f;

    public LASReaderOptionsBuilder withMaxNumberOfVertices(int numberOfVertices)
    {
        maxNumberOfVertices = numberOfVertices;
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
