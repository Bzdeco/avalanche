package backend.rasterizer;

public class CurvatureGridTask extends ChainTask<float[][]> {
    private float[][] dem;
    private float cellSize;

    public CurvatureGridTask(float[][] dem, double cellSize) {
        this.dem = dem;
        this.cellSize = (float)cellSize;
    }

    @Override
    public float[][] call() {
        int nRows = dem.length;
        int nCols = dem[0].length;

        float results[][] = new float[nRows][nCols];
        float L = cellSize, L2 = L * L, L3 = L2 * L, L4 = L3 * L;

        float norm = 0;

        float z[] = new float[9];
        int i;
        for (int y = 0; y < nRows; y++) {
            for (int x = 0; x < nCols; x++) {
                i = 0;
                for(int dy = -1; dy < 1; ++dy) {
                    for(int dx = -1; dx < 1; ++dx) {
                        int fx = x + dx, fy = y + dy;
                        z[i++] = (fx >= 0 && fx < nCols && fy >= 0 && fy < nRows) ? dem[fy][fx] : 0f;
                    }
                }
                float fx = (float)x, fy = (float)y, x2 = fx*fx, y2 = fy*fy;

                float A = ((z[0] + z[2] + z[6] + z[8]) / 4  - (z[1] + z[3] + z[5] + z[7]) / 2 + z[4]) / L4;
                float B = ((z[0] + z[2] - z[6] - z[8]) /4 - (z[1] - z[7]) /2) / L3;
                float C = ((-z[0] + z[2] - z[6] + z[8]) /4 + (z[3] - z[5]) /2) / L3;
                float D = ((z[3] + z[5]) / 2 - z[4]) / L2;
                float E = ((z[1] + z[7]) / 2 - z[4]) / L2;
                float F = (-z[0] + z[2] + z[6] - z[8]) / 4*L2;
                float G = (-z[3] + z[5]) / 2L;
                float H = (z[1] - z[7]) / 2L;
                float I = z[4];

                float Z = A * x2 * y2 + B * x2 * fy + C * fx * y2
                        + D * x2      + E * y2      + F * fx * fy
                        + G * fx      + H * fy      + I;

                if(!Float.isNaN(Z)) norm = Math.max(norm, Math.abs(Z));
                results[y][x] = Z;
            }
        }


        for (int y = 0; y < nRows; y++) {
            for (int x = 0; x < nCols; x++) {
                results[y][x] = results[y][x] / norm;
            }
        }

        return results;
    }
}
