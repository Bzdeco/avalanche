package org.avalanche.controller;

import com.google.common.collect.ImmutableList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import las2etin.display.TerrainFormatter;
import las2etin.model.GeographicCoordinates;
import las2etin.model.StaticMapNameToGeoBoundsConverter;
import las2etin.model.Terrain;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.avalanche.model.database.WeatherConnector;
import org.avalanche.model.database.WeatherDto;
import org.avalanche.model.risk.Risk;
import org.avalanche.view.Printer;
import org.avalanche.view.layers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import weatherCollector.coordinates.Coords;
import weatherCollector.coordinates.StaticMapNameToCoordsConverter;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Main application controller setting the UI, displaying fetched weather and drawing generated terrain and risk
 * images in the UI
 */
@Controller
@Log4j2
public class FXMLController {
    private List<TerrainLayer> terrainLayers;
    private List<RiskLayer> riskLayers = ImmutableList.of();

    public final StaticMapNameToGeoBoundsConverter converter = new StaticMapNameToGeoBoundsConverter();
    private final WeatherConnector weatherConnector;

    @FXML
    private ProgressBar globalRisk;

    @FXML
    private TreeView layerSelector;

    @FXML
    private Pane layerViewport;

    @FXML
    private TableView tableView;

    @Autowired
    public FXMLController(final WeatherConnector weatherConnector) {
        this.weatherConnector = weatherConnector;
    }

    /**
     * Method called when main application window is launched
     */
    @FXML
    public void initialize() {
        final File file = selectFile();

        if (file != null) {
            collectWeatherDataToDatabase(file.getName());

            final Terrain terrain = TerrainFormatter.deserialize(file.toPath());
            final GeographicCoordinates centerCoords = terrain.getCenterCoords()
            AvalancheRiskController avalancheRiskController = new AvalancheRiskController(
                    weatherConnector,
                    terrain,
                    centerCoords);

            terrainLayers = ImmutableList.of(
                    new LandformLayer("Landform"),
                    new SlopeLayer("Slope"),
                    new SusceptiblePlacesLayer("Susceptible places"),
                    new HillshadeLayer("Hillshade", centerCoords)
            );

            riskLayers = ImmutableList.of(
                    new AvalancheRiskLayer("Avalanche risk")
            );

            List<WeatherDto> weatherConditions = avalancheRiskController.fetchWeatherDataInto(tableView);
            final Risk risk = avalancheRiskController.getEvaluatedRisk(weatherConditions);

            new Printer(terrain, risk).drawOnPane(layerViewport, terrainLayers, riskLayers, layerSelector);
            float globalRiskValue = avalancheRiskController.getGlobalRiskValue();
            globalRisk.setProgress(globalRiskValue);
        }
    }

    /**
     * Gets most recent 5-day weather forecast to local database
     */
    private void collectWeatherDataToDatabase(String filename) {
        OkHttpClient avalancheClient = new OkHttpClient();
        String requestURL = String.format("http://localhost:8080/getWeatherData?filename=%s", filename);
        Request request = new Request.Builder()
                .url(requestURL)
                .build();

        try {
            avalancheClient.newCall(request).execute();
        } catch (IOException e) {
            log.warn("Could not update weather data in the database");
        }
    }

    private File selectFile() {
        try {
            return trySelectFile();

        } catch (OperationNotSupportedException ex) {
            Platform.exit();
            return null;
        }
    }

    private File trySelectFile() throws OperationNotSupportedException {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Serialized terrain model", "*.ser"));
        fileChooser.setTitle("Choose serialized terrain model file (.ser)");
        final File file = fileChooser.showOpenDialog(null);
        validateFileSelection(file);
        return file;
    }

    private void validateFileSelection(final File file) throws OperationNotSupportedException {
        if (file == null || !file.exists()) {
            log.error("User cancelled file selection");
            throw new OperationNotSupportedException("You have to select a file to proceed");
        }
    }
}
