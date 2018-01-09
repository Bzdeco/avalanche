package las2etin.display;

import las2etin.display.layers.LandformLayer;
import las2etin.display.layers.SlopeLayer;
import las2etin.display.layers.SusceptiblePlacesLayer;
import las2etin.model.Terrain;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TerrainPrinterTest
{
    @Test
    @Ignore("For demonstration purposes")
    public void printLandform() throws Exception
    {
        Path saveLocation = Paths.get("src/test/resources/test.ser");
        Terrain terrain = TerrainFormatter.deserialize(saveLocation);
        TerrainPrinter printer = new TerrainPrinter(terrain);
        printer.print(new LandformLayer());
    }

    @Test
    @Ignore("For demonstration purposes")
    public void printSlope() throws Exception
    {
        Path saveLocation = Paths.get("src/test/resources/test.ser");
        Terrain terrain = TerrainFormatter.deserialize(saveLocation);
        TerrainPrinter printer = new TerrainPrinter(terrain);
        printer.print(new SlopeLayer());
    }

    @Test
    @Ignore("For demonstration purposes")
    public void printSusceptiblePlaces() throws Exception
    {
        Path saveLocation = Paths.get("src/test/resources/test.ser");
        Terrain terrain = TerrainFormatter.deserialize(saveLocation);
        TerrainPrinter printer = new TerrainPrinter(terrain);
        printer.print(new SusceptiblePlacesLayer());
    }
}