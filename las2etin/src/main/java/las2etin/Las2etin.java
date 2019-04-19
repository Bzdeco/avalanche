package las2etin;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import las2etin.display.TerrainFormatter;
import las2etin.exception.LasFileException;
import las2etin.las.LASFile;
import las2etin.las.LASReader;
import las2etin.model.*;
import las2etin.tin.Tin;
import las2etin.tin.TinBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tinfour.common.Vertex;
import weatherCollector.coordinates.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Las2etin
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Las2etin.class);
	public final StaticMapNameToCoordsConverter converter = new StaticMapNameToCoordsConverter();


	@Parameter(names = {"--input", "-i"}, description = "LAS file to be converted", required = true)
    private String lasFilepath;

    @Parameter(names = {"--resolution", "-r"}, description = "Number of probed points across one direction (max 500)." +
            " Total number of probed points is equal to this argument squared (probes are taken in X and Y direction).")
    int resolution = 500;

    @Parameter(names = {"--help", "-h"}, help = true)
    private boolean help = false;

    private String serFilepath;

    public static void main(String[] args)
    {
        Las2etin las2etin = new Las2etin();
        JCommander jCommander = JCommander.newBuilder().addObject(las2etin).build();
        jCommander.parse(args);

        if(las2etin.help)
        {
            jCommander.usage();
        }
        else {
            run(las2etin);
        }
    }

    private static void run(Las2etin las2etin)
    {
        try {
            las2etin.serialize();
        }
        catch (LasFileException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void serialize() throws LasFileException
    {
        LASFile lasFile = tryLocatingLasFile();
        LASReader reader = LASReader.createFor(lasFile);
        reportProgress("Reading file...");
        List<Vertex> readVertices = tryReadingLasFileContent(reader);
        Bounds bounds = reader.getVerticesBounds();
        reportProgress("Building terrain mesh...");
        Tin terrainMesh = new TinBuilder().withVertices(readVertices).withBounds(bounds).build();
        trimResolution();

        TerrainSettings settings = new TerrainSettingsBuilder().withWidthInCells(resolution)
                                                               .withHeightInCells(resolution)
                                                               .build();
        Coords centerCoords = converter.convert(lasFilepath);
        Terrain terrainToSerialize = new TerrainBuilder(terrainMesh).withSettings(settings)
																	.withCenterCoordinates(centerCoords)
																	.build();

        setSavePathToDefault();

        Path saveLocation = Paths.get(serFilepath);
        reportProgress("Saving terrain to file...");
        TerrainFormatter.serialize(terrainToSerialize, saveLocation);
    }

    private void trimResolution()
    {
        if (resolution > 500 || resolution < 0)
            resolution = 500;
    }

    private void setSavePathToDefault()
    {
        int fileExtensionLength = 4;
        serFilepath = String.format("%s.ser", lasFilepath.substring(0, lasFilepath.length() - fileExtensionLength));
    }

    private LASFile tryLocatingLasFile() throws LasFileException
    {
        try {
            return LASFile.fromFilePath(lasFilepath);
        }
        catch (FileNotFoundException e) {
            throw new LasFileException("Input file does not exist", e);
        }
    }

    private void reportProgress(String message)
    {
        System.out.println(message);
    }

    private List<Vertex> tryReadingLasFileContent(LASReader reader) throws LasFileException
    {
        try {
            return reader.getVerticesRecords();
        }
        catch (IOException e) {
            throw new LasFileException("Could not read vertex records from input file", e);
        }
    }
}
