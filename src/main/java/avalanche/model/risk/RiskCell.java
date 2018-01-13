package avalanche.model.risk;

import las2etin.model.Coordinates;

/**
 * Created by annterina on 13.01.18.
 */
public class RiskCell {

    private final Coordinates coordinates;
    private float maxRisk = 0f;

    public RiskCell(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public double getMaxRisk() {
        return maxRisk;
    }

    public void setMaxRisk(float maxRisk) {
        this.maxRisk = maxRisk;
    }
}
