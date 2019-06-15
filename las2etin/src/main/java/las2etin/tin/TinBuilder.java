package las2etin.tin;

import las2etin.model.Bounds;
import las2etin.tin.exception.TINBuildingException;
import org.tinfour.common.IIncrementalTin;
import org.tinfour.common.Vertex;
import org.tinfour.semivirtual.SemiVirtualIncrementalTin;
import org.tinfour.utils.HilbertSort;
import org.tinfour.utils.Tincalc;

import java.util.ArrayList;
import java.util.List;

public final class TinBuilder
{
	private static final int MAX_VERTICES_IN_TIN = 100000;
	private IIncrementalTin incrementalTin;
    private Bounds bounds;
    private List<Vertex> vertices;

    public TinBuilder()
    {
        incrementalTin = new SemiVirtualIncrementalTin();
    }

    public TinBuilder withVertices(List<Vertex> vertices)
    {
    	this.vertices = vertices;
    	return this;
    }

    public TinBuilder withBounds(Bounds bounds)
    {
        this.bounds = bounds;
        return this;
    }

    public Tin build()
    {
    	List<Vertex> thinList = reduceVerticesNumber(vertices, bounds);
		boolean isBuildSuccessful = incrementalTin.add(vertices, null); // RIP
		if (isBuildSuccessful)
			return new Tin(incrementalTin, bounds);
		else
			throw new TINBuildingException("Building incremental TIN failed");
    }

	private List<Vertex> reduceVerticesNumber(List<Vertex> list, Bounds bounds)
	{
		if (list.size() > 16)
		{
			HilbertSort hilbertSort = new HilbertSort();
			hilbertSort.sort(list);
		}

		int nVertices = list.size();
		double mx0 = bounds.getMinX();
		double my0 = bounds.getMinY();
		double mx1 = bounds.getMaxX();
		double my1 = bounds.getMaxY();
		double area = (mx1 - mx0) * (my1 - my0);
		double nominalPointSpacing = Tincalc.sampleSpacing(area, nVertices);

		incrementalTin = new SemiVirtualIncrementalTin(nominalPointSpacing);

		List<Vertex> thinList = new ArrayList<>(MAX_VERTICES_IN_TIN + 500);
		if (nVertices <= MAX_VERTICES_IN_TIN) {
			return thinList;
		} else {
			// we're going to step through the list skipping a bunch of
			// vertices.  because the list is Hilbert sorted, the vertices that
			// do get selected should still  give  a pretty good coverage
			// of the overall area of the TIN.
			double s = (double) nVertices / (double) MAX_VERTICES_IN_TIN;
			int priorIndex = -1;
			for (int i = 0; i < MAX_VERTICES_IN_TIN; i++) {
				int index = (int) (i * s + 0.5);
				if (index > priorIndex) {
					thinList.add(list.get(index));
					priorIndex = index;
				}
			}
			if (priorIndex != nVertices - 1) {
				thinList.add(list.get(nVertices - 1));
			}

			// ensure that the perimeter is fully formed by adding
			// any points that are not inside the tin.  In testing,
			// the number of points to be added has been less than
			// a couple hundred even for data sets containing millions
			// of samples. So this should execute fairly quickly,
			// especially since the list has been Hilbert sorted and has a
			// high degree of spatial autocorrelation.
			for (Vertex v : list) {
				if (!incrementalTin.isPointInsideTin(v.getX(), v.getY())) {
					thinList.add(v);
				}
			}
		}

		return thinList;
	}
}
