package las2etin.tin;

import las2etin.las.vertex.Bounds;
import tinfour.common.IIncrementalTin;

public class Tin
{
    private IIncrementalTin incrementalTin;
    private Bounds bounds;

    Tin(IIncrementalTin incrementalTin, Bounds bounds)
    {
        this.incrementalTin = incrementalTin;
        this.bounds = bounds;
    }

    public IIncrementalTin getIncrementalTin()
    {
        return incrementalTin;
    }

    public Bounds getBounds()
    {
        return bounds;
    }
}
