package las2etin.tin;

import las2etin.tin.exception.TINBuildingException;
import tinfour.common.IIncrementalTin;
import tinfour.common.Vertex;
import tinfour.standard.IncrementalTin;

import java.util.List;

public final class TINBuilder
{
    private IIncrementalTin incrementalTin;

    public TINBuilder()
    {
        incrementalTin = new IncrementalTin();
    }

    public IIncrementalTin buildFrom(List<Vertex> vertices)
    {
        boolean isBuildSuccessful = incrementalTin.add(vertices, null); // RIP

        if (isBuildSuccessful)
            return incrementalTin;
        else
            throw new TINBuildingException("Building incremental TIN failed");
    }
}
