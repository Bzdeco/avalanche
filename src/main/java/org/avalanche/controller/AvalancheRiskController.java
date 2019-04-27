package org.avalanche.controller;

import com.sun.javafx.util.Utils;
import javafx.scene.control.TableView;
import las2etin.model.GeographicCoordinates;
import las2etin.model.Terrain;
import las2etin.model.TerrainCell;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.sun.javafx.util.Utils;
import net.e175.klaus.solarpositioning.AzimuthZenithAngle;
import net.e175.klaus.solarpositioning.DeltaT;
import net.e175.klaus.solarpositioning.Grena3;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.avalanche.model.database.WeatherConnector;
import org.avalanche.model.database.WeatherDto;
import org.avalanche.model.risk.LocalRiskEvaluator;
import org.avalanche.model.risk.Risk;
import org.avalanche.model.risk.RiskCell;
import weatherCollector.coordinates.Coords;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

@Log4j2
public class AvalancheRiskController {

    private final WeatherConnector weatherConnector;

    private Risk risk;
    private Terrain terrain;
    private GeographicCoordinates geographicCoordinates;

    public AvalancheRiskController(final WeatherConnector weatherConnector,
                                   final Terrain terrain,
                                   final GeographicCoordinates geographicCoordinates)) {
        this.weatherConnector = weatherConnector;
        this.terrain = terrain;
        this.risk = new Risk(terrain);
        this.geographicCoordinates = geographicCoordinates;
    }

    public float getGlobalRiskValue()
    {
        return risk.getGlobalRiskValue();
    }

    public Risk getEvaluatedRisk(List<WeatherDto> weatherConditions)
    {
        log.info("Risk evaluation started");
        risk.predictGlobalRiskValue(weatherConditions);
        risk.predictLocalRisks(new LocalRiskEvaluator(weatherConditions));
        log.info("Risk evaluation finished successfully");

        return risk;
    }

    public List<WeatherDto> fetchWeatherDataInto(TableView tableView)
    {
        weatherConnector.setTableView(tableView);
        return weatherConnector.fetchAndBuildData();
    }
}
