package backend.rasterizer;

public class SteepnessGridTask extends ChainTask<float[][]> {
    private float[][][] normVectors;

    public SteepnessGridTask(float[][][] normalVector) {
        this.normVectors = normalVector;
    }

    @Override
    public float[][] call() {
        return Utils.gmap2f(normVectors, n -> {
            if(n[0] == -1) return 0f;
            float r = (float)Math.sqrt(n[0] * n[0] + n[1] * n[1] + n[2] * n[2]);
            return (float)Math.acos(n[2] / r);
        });
    }
}
