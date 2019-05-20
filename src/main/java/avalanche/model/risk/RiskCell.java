package avalanche.model.risk;

import las2etin.model.Coordinates;
import las2etin.model.TerrainCell;

public class RiskCell {

    private final TerrainCell terrainCell;
    private float riskValue = 0f;

    public RiskCell(TerrainCell terrainCell) {
        this.terrainCell = terrainCell;
    }

    public Coordinates getCoordinates() {
        return terrainCell.getCoordinates();
    }

    public void evaluateRisk(LocalRiskEvaluator localRiskEvaluator, float globalRiskValue)
    {
    	float localRiskValue = localRiskEvaluator.evaluate(terrainCell);
        riskValue = localRiskValue > 0 ? (localRiskValue + globalRiskValue) / 2 : 0f;
    }

    public float getRiskValue() {
        return riskValue;
    }

    public void setRiskValue(float riskValue) {
        this.riskValue = riskValue;
    }

    public int getX()
    {
        return terrainCell.getX();
    }

    public int getY()
    {
        return terrainCell.getY();
    }
}
