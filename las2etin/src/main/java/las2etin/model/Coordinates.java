package las2etin.model;

import java.io.Serializable;

public class Coordinates implements Serializable
{
    private static final long serialVersionUID = 4596453708352625490L;

    private int x;
    private int y;

    public Coordinates(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Coordinates that = (Coordinates) o;

        if (x != that.x)
            return false;
        return y == that.y;
    }

    @Override
    public int hashCode()
    {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
