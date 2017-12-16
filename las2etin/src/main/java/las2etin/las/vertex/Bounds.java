package las2etin.las.vertex;

public class Bounds
{
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;

    public Bounds(double minX, double minY, double maxX, double maxY)
    {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public double getWidth()
    {
        return maxX - minX;
    }

    public double getHeight()
    {
        return maxY - minY;
    }

    public double getMinX()
    {
        return minX;
    }

    public double getMinY()
    {
        return minY;
    }

    public double getMaxX()
    {
        return maxX;
    }

    public double getMaxY()
    {
        return maxY;
    }
}
