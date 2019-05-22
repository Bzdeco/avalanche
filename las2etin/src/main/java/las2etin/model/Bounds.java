package las2etin.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class Bounds implements Serializable
{
    private static final long serialVersionUID = -2564908662911290297L;

    private final double minX;
    private final double minY;
    private final double maxX;
    private final double maxY;
    private final double minZ;
    private final double maxZ;

    double getWidth()
    {
        return maxX - minX;
    }

    double getHeight()
    {
        return maxY - minY;
    }
}
