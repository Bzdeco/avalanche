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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Tin tin = (Tin) o;

        if (!incrementalTin.equals(tin.incrementalTin))
            return false;
        return bounds.equals(tin.bounds);
    }

    @Override
    public int hashCode()
    {
        int result = incrementalTin.hashCode();
        result = 31 * result + bounds.hashCode();
        return result;
    }
}
