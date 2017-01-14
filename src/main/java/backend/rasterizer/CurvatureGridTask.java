package backend.rasterizer;

public class CurvatureGridTask extends ChainTask<float[][]> {
    private float[][] st;

    public CurvatureGridTask(float[][] steepness) {
        this.st = steepness;
    }

    @Override
    public float[][] call() {
        int nRows = st.length;
        int nCols = st[0].length;

        float results[][] = new float[nRows][nCols];

        float norm = 0;

        float z[] = new float[9];
        int i;
        for (int y = 1; y < nRows - 1; y++) {
            for (int x = 1; x < nCols - 1; x++) {
                i = 0;
                for(int dy = -1; dy < 1; ++dy) {
                    for(int dx = -1; dx < 1; ++dx) {
                        int fx = x + dx, fy = y + dy;
                        z[i++] = st[fy][fx];
                    }
                }

                float curv = (z[1] + z[3] + z[5] + z[7]) / 2 - 2 * z[4];
                norm = Math.max(norm, Float.isNaN(curv) ? 0 : Math.abs(curv));
                results[y][x] = curv;
            }
        }

        for (int y = 0; y < nRows; y++) {
            for (int x = 0; x < nCols; x++) {
                results[y][x] /= norm;
            }
        }

        return results;
    }
}
