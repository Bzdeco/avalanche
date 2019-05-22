package las2etin.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode
public class TerrainCell implements Serializable
{
    private static final long serialVersionUID = -5616958564343992038L;

    private final Coordinates coordinates;
    private final GeographicCoordinates geographicCoords;
    private final Vector3D normal;
    private final double aspect;
    private final double grade;
    private final double slope;
    private final double planCurvature;
    private final double profileCurvature;

    public int getX()
    {
        return coordinates.getX();
    }

    public int getY()
    {
        return coordinates.getY();
    }
}
