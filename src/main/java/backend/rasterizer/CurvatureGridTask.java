package backend.rasterizer;

public class CurvatureGridTask extends ChainTask<float[][]> {
    private float[][] dem;

    public CurvatureGridTask(float[][] dem) {
        this.dem = dem;
    }

    @Override
    public float[][] call() throws Exception {
        return dem; // Temporary
    }
}
