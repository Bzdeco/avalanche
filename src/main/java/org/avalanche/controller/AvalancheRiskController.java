package org.avalanche.controller;

import javafx.scene.control.TableView;
import las2etin.model.GeographicCoordinates;
import las2etin.model.Terrain;
import lombok.extern.log4j.Log4j2;
import org.avalanche.model.database.WeatherConnector;
import org.avalanche.model.database.WeatherDto;
import org.avalanche.model.risk.LocalRiskEvaluator;
import org.avalanche.model.risk.Risk;

import java.util.List;

@Log4j2
public class AvalancheRiskController {

    private final WeatherConnector weatherConnector;

    private Risk risk;
    private Terrain terrain;
    private GeographicCoordinates geographicCoordinates;

    public AvalancheRiskController(final WeatherConnector weatherConnector,
                                   final Terrain terrain,
                                   final GeographicCoordinates geographicCoordinates) {
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
