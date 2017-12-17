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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Bounds bounds = (Bounds) o;

        if (Double.compare(bounds.minX, minX) != 0)
            return false;
        if (Double.compare(bounds.minY, minY) != 0)
            return false;
        if (Double.compare(bounds.maxX, maxX) != 0)
            return false;
        return Double.compare(bounds.maxY, maxY) == 0;
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        temp = Double.doubleToLongBits(minX);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(minY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(maxX);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(maxY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
