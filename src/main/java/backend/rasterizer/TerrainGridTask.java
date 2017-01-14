package backend.rasterizer;

public class TerrainGridTask extends ChainTask<float[][]> {
    private float[][][] normVectors;

    public TerrainGridTask(float[][][] normVectors) {
        this.normVectors = normVectors;
    }

    public float[][] call() {
        return Utils.gmap2f(normVectors, n -> n[3]);
    }
}
