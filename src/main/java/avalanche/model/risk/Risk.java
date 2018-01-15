package avalanche.model.risk;

import las2etin.model.Coordinates;
import las2etin.model.Terrain;
import las2etin.model.TerrainCell;

import java.util.*;

/**
 * Created by annterina on 13.01.18.
 */
public class Risk {

    private Map<Integer, List<RiskCell>> riskCells = new HashMap<>();

    public Risk(Terrain terrain) {
        for (Map.Entry<Integer, List<TerrainCell>> entry : terrain.getTerrainCells().entrySet()) {
            List<RiskCell> riskCellsRow = new ArrayList<>();
            for (TerrainCell terrainCell: entry.getValue()) {
                riskCellsRow.add(new RiskCell(terrainCell));
            }
            riskCells.put(entry.getKey(), riskCellsRow);
        }
    }

    public RiskCell getRiskCellWithCoordinates(Coordinates coordinates)
    {
        int x = coordinates.getX();
        int y = coordinates.getY();

        List<RiskCell> searchedRow = riskCells.get(x);
        return searchedRow.get(y);
    }

    public Map<Integer, List<RiskCell>> getRiskCells()
    {
        return riskCells;
    }

    public String getAllRiskValues() {
        String values = "";
        for (Map.Entry<Integer, List<RiskCell>> entry : riskCells.entrySet()) {
            for (RiskCell riskCell : entry.getValue()) {
                values += riskCell.getRiskValue() + " ";
            }
            values += "\n";
        }
        return values;
    }


}
