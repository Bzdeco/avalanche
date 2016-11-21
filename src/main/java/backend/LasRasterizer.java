package backend;

import backend.parsers.las.LasFileReader;
import backend.parsers.las.LasPoint;

import java.io.File;
import java.io.IOException;

public class LasRasterizer {
    public static double[][] rasterize(File lasfile) throws IOException {
        LasPoint p = new LasPoint();
        LasFileReader lf = new LasFileReader(lasfile);

        long recordCount = lf.getNumberOfPointRecords();

        double minX = lf.getMinX(), spanX = lf.getMaxX() - minX;
        double minY = lf.getMinY(), spanY = lf.getMaxY() - minY;
        double minZ = lf.getMinZ(), spanZ = lf.getMaxZ() - minZ;

        final double matrixWidth = 1024, matrixHeight = 1024;

        double data[][] = new double[(int)matrixHeight][(int)matrixWidth];

        for(long index = 0; index < recordCount; ++index) {
            lf.readRecord(index, p);
            final int x = (int)Math.round((p.x - minX) / spanX * (matrixWidth - 1));
            final int y = (int)Math.round((p.y - minY) / spanY * (matrixHeight - 1));
            data[y][x] = (p.z - minZ) / spanZ;
        }

        return data;
    }
}

