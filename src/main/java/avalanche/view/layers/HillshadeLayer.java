package avalanche.view.layers;

import com.sun.javafx.util.Utils;
import las2etin.model.TerrainCell;
import net.e175.klaus.solarpositioning.AzimuthZenithAngle;
import net.e175.klaus.solarpositioning.DeltaT;
import net.e175.klaus.solarpositioning.Grena3;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import weatherCollector.coordinates.Coords;

import java.awt.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Layer for displaying how the terrain is highlighted by the sun at noon on a current noon.
 */
public class HillshadeLayer implements TerrainLayer
{
    private final String name;
    private final Coords geographicalCoordinates;

    public HillshadeLayer(String name, Coords geographicalCoordinates)
    {
        this.name = name;
        this.geographicalCoordinates = geographicalCoordinates;
    }

    @Override
    public void drawCell(Graphics2D graphics, TerrainCell cell)
    {
        GregorianCalendar todayNoon = getTodayNoonTime();
        float sunAngle = getSunAngle(cell.getNormal(), todayNoon);
        float hillshade = calculatedHillshade(sunAngle);

        Color pixelColor = getColorFromHillshade(hillshade);

        Shape cellRectangle = new Rectangle(cell.getX(), cell.getY(), 1, 1);
        graphics.setPaint(pixelColor);
        graphics.draw(cellRectangle);
        graphics.fill(cellRectangle);
    }

    private Color getColorFromHillshade(float hillshade)
    {
        return new Color(hillshade, hillshade, hillshade);
    }

    private float calculatedHillshade(float sunAngle)
    {
        float ambient = 0.25f;
        float directLight = 1f - ambient;

        return Utils.clamp(0, sunAngle * directLight + ambient, 1);
    }

    private float getSunAngle(Vector3D terrainCellNormal, GregorianCalendar todayNoon)
    {
        Float latitude = geographicalCoordinates.getLatitude();
        Float longitude = geographicalCoordinates.getLongitude();
        AzimuthZenithAngle solarPosition = Grena3.calculateSolarPosition(
                todayNoon,
                latitude,
                longitude,
                DeltaT.estimate(todayNoon));

        double sunAzimuth = Math.toRadians(solarPosition.getAzimuth());
        double sunZenith = Math.toRadians(solarPosition.getZenithAngle());
        double cosA = Math.cos(sunAzimuth);
        double sinA = Math.sin(sunAzimuth);
        double cosE = Math.sin(sunZenith); // Inverted (sin, cos) because we want elevation
        double sinE = Math.cos(sunZenith);
        double xSun = cosA * cosE;
        double ySun = sinA * cosE;
        double zSun = sinE;

        return (float) Math.max(0, terrainCellNormal.getX() * xSun
                + terrainCellNormal.getY() * ySun + terrainCellNormal.getZ() * zSun);
    }

    private GregorianCalendar getTodayNoonTime()
    {
        GregorianCalendar todayNoon = new GregorianCalendar();
        todayNoon.set(Calendar.HOUR_OF_DAY, 12);
        todayNoon.set(Calendar.MINUTE, 0);
        todayNoon.set(Calendar.SECOND, 0);
        return todayNoon;
    }

    @Override
    public String name()
    {
        return "Hillshade";
    }
}
