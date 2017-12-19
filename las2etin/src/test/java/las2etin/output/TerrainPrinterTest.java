package las2etin.output;

import las2etin.model.Terrain;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class TerrainPrinterTest
{
    @Test
    public void printLandform() throws Exception
    {
        Path saveLocation = Paths.get("src/test/resources/test.ser");
        Terrain terrain = TerrainFormatter.deserialize(saveLocation);
        TerrainPrinter printer = new TerrainPrinter(terrain);
        printer.printLandform();
    }

    @Test
    public void printSlope() throws Exception
    {
        Path saveLocation = Paths.get("src/test/resources/test.ser");
        Terrain terrain = TerrainFormatter.deserialize(saveLocation);
        TerrainPrinter printer = new TerrainPrinter(terrain);
        printer.printSlope();
    }

    @Test
    public void printSusceptiblePlaces() throws Exception
    {
        Path saveLocation = Paths.get("src/test/resources/test_lowres.ser");
        Terrain terrain = TerrainFormatter.deserialize(saveLocation);
        TerrainPrinter printer = new TerrainPrinter(terrain);
        printer.printSusceptiblePlaces();
    }
}