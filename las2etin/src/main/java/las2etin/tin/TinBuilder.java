package las2etin.tin;

import las2etin.las.vertex.Bounds;
import las2etin.tin.exception.TINBuildingException;
import tinfour.common.IIncrementalTin;
import tinfour.common.Vertex;
import tinfour.standard.IncrementalTin;

import java.util.List;

public final class TinBuilder
{
    private IIncrementalTin incrementalTin;
    private Bounds bounds;

    public TinBuilder()
    {
        incrementalTin = new IncrementalTin();
    }

    public TinBuilder withVertices(List<Vertex> vertices)
    {
        boolean isBuildSuccessful = incrementalTin.add(vertices, null); // RIP

        if (isBuildSuccessful)
            return this;
        else
            throw new TINBuildingException("Building incremental TIN failed");
    }

    public TinBuilder withBounds(Bounds bounds)
    {
        this.bounds = bounds;
        return this;
    }

    public Tin build()
    {
        return new Tin(incrementalTin, bounds);
    }
}
