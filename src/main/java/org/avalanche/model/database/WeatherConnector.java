package org.avalanche.model.database;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Class used for connecting with database and fetching weather measurements as well as attaching them to UI
 */
@Service
@Log4j2
public class WeatherConnector {

    @Value("${avalanche.weather.database.url}")
    private String dbUrl;

    @Value("${avalanche.weather.database.username}")
    private String dbUsername;

    @Value("${avalanche.weather.database.password}")
    private String dbPassword;

    private Connection connection;
    private TableView tableView;

    public void setTableView(TableView table) {
        this.tableView = table;
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            log.info("Opened database successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(0);
        }
    }

    public List<WeatherDto> fetchAndBuildData() {
        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        List<WeatherDto> weatherDtoList = new LinkedList<>();

        try {
            if (connection == null) connect();

            String query = "select * from weather order by time";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            // Initialize TableView with column headers
            if (tableView.getColumns().size() == 0) {

                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    // We are using non property style for making dynamic table
                    final int j = i;
                    TableColumn tableColumn = new TableColumn(resultSet.getMetaData().getColumnName(i + 1));
                    // noinspection unchecked
                    tableColumn.setCellValueFactory(param -> {
                        @SuppressWarnings("unchecked")
                        Object property = ((TableColumn.CellDataFeatures<ObservableList, String>) param).getValue().get(j);
                        return new SimpleStringProperty(property != null ? property.toString() : "null");
                    });

                    tableView.getColumns().addAll(tableColumn);
                    log.debug("Column [{}] ", i);
                }
            }

            // Data added to ObservableList
            int count = 1;
            while (resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    row.add(resultSet.getString(i));
                }
                log.debug("Row [{}] added {}", count++, row);
                data.add(row);

                WeatherDto weatherDto = new WeatherDto.Builder().build(row);
                weatherDtoList.add(weatherDto);

            }

            tableView.setItems(data);

        } catch (Exception e) {
            log.error("Failed to build weather list", e);
        }
        return weatherDtoList;
    }
}
