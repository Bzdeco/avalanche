package avalanche.controller;

import avalanche.model.database.WeatherConnector;
import avalanche.model.database.WeatherDto;
import avalanche.model.risk.LocalRiskEvaluator;
import avalanche.model.risk.Risk;
import avalanche.model.risk.RiskCell;
import javafx.scene.control.TableView;
import las2etin.model.GeographicCoordinates;
import las2etin.model.Terrain;
import las2etin.model.TerrainCell;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.sun.javafx.util.Utils;
import net.e175.klaus.solarpositioning.AzimuthZenithAngle;
import net.e175.klaus.solarpositioning.DeltaT;
import net.e175.klaus.solarpositioning.Grena3;

import java.util.*;

public class AvalancheRiskController {

    private static final Logger LOGGER = LogManager.getLogger();

    private Risk risk;
    private Terrain terrain;
    private GeographicCoordinates geographicCoordinates;

    public AvalancheRiskController(final Terrain terrain, final GeographicCoordinates geographicCoordinates)
    {
        this.risk = new Risk(terrain);
        this.terrain = terrain;
        this.geographicCoordinates = geographicCoordinates;
    }

    public float getGlobalRiskValue()
    {
        return risk.getGlobalRiskValue();
    }

    public Risk getEvaluatedRisk(List<WeatherDto> weatherConditions)
    {
        LOGGER.info("Risk evaluation started");
        risk.predictGlobalRiskValue(weatherConditions);
        risk.predictLocalRisks(new LocalRiskEvaluator(weatherConditions));
        LOGGER.info("Risk evaluation finished successfully");

        return risk;
    }

    public List<WeatherDto> fetchWeatherDataInto(TableView tableView)
    {
        WeatherConnector connector = WeatherConnector.getInstance();
        connector.setTableView(tableView);
        return connector.fetchAndBuildData();
    }
}
