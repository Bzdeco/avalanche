package avalanche.model.risk;

import las2etin.model.Coordinates;
import las2etin.model.TerrainCell;

/**
 * Created by annterina on 13.01.18.
 */
public class RiskCell {

    private final TerrainCell terrainCell;
    private float riskValue = 0f;

    public RiskCell(TerrainCell terrainCell) {
        this.terrainCell = terrainCell;
    }

    public Coordinates getCoordinates() {
        return terrainCell.getCoordinates();
    }

    public double getRiskValue() {
        return riskValue;
    }

    public void evaluateLocalRisk(LocalRiskEvaluator localRiskEvaluator)
    {
        riskValue = localRiskEvaluator.evaluate(terrainCell);
    }

    public void setRiskValue(float riskValue) {
        this.riskValue = riskValue;
    }
}
