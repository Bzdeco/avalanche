package backend.rasterizer.tasks;

import backend.rasterizer.TerrainProps;
import backend.rasterizer.Utils;
import javafx.concurrent.Task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AvalancheRisk extends Task<float[][]> {
    private Future<float[][][]> fterrain;
    private Future<float[][]> fhillshade;

    public AvalancheRisk(Future<float[][][]> terrain, Future<float[][]> hillshade) {
        fterrain = terrain;
        fhillshade = hillshade;
    }

    @Override
    public float[][] call() throws ExecutionException, InterruptedException {
        float[][][] terrain = fterrain.get();
        float[][] hillshade = fhillshade.get();

        int nRows = terrain.length;
        int nCols = terrain[0].length;

        float results[][] = new float[nRows][nCols];

        for (int iRow = 0; iRow < nRows; iRow++) {
            for (int iCol = 0; iCol < nCols; iCol++) {
                // wszystkie parametry modelu terenu są dostępne jako np. terrain[TerrainProps.ALTITUDE]
                // Zapisujemy ryzyko lawinowe - liczba z zakresu 1 - 5, jak w TPN
                results[iRow][iCol] = 5f;
            }
        }

        return results;
    }
}
