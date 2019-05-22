package org.avalanche.model.risk;

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

    public void evaluateLocalRisk(LocalRiskEvaluator localRiskEvaluator)
    {
        riskValue = localRiskEvaluator.evaluate(terrainCell);
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
