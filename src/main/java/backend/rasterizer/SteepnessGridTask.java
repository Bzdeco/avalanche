package backend.rasterizer;

import javafx.concurrent.Task;
import tinfour.testutils.GridSpecification;
import tinfour.virtual.VirtualIncrementalTin;


public class SteepnessGridTask extends ChainTask<float[][]> {
    private float[][][] normVectors;

    public SteepnessGridTask(float[][][] normalVector) {
        this.normVectors = normalVector;
    }

    @Override
    public float[][] call() {
        float[][] result = Utils.gmap2f(normVectors, n -> {
            if(n[0] == -1) return 0f;
            // https://en.wikipedia.org/wiki/Spherical_coordinate_system#Cartesian_coordinates
            double r = Math.sqrt(n[0] * n[0] + n[1] * n[1] + n[2] * n[2]);
            return (float)Math.acos(n[2] / r);
        });
        return result;
    }
}
