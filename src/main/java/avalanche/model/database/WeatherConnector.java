package avalanche.model.database;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import static avalanche.controller.ResourceHandler.getDbDriver;
import static avalanche.controller.ResourceHandler.getDbPass;
import static avalanche.controller.ResourceHandler.getDbUrl;
import static avalanche.controller.ResourceHandler.getDbUser;

public class WeatherConnector {
    private static final Logger LOGGER = LogManager.getLogger();

    private Connection connection;
    private PreparedStatement statement;
    private TableView tableView;

    private WeatherConnector(){}

    public static WeatherConnector getInstance(){
        return LazyHandler.instance;
    }

    public void setTableView(TableView table) {
        this.tableView = table;
    }

    private void connect() {
        try {
            Class.forName(getDbDriver());
            connection = DriverManager.getConnection(getDbUrl(), getDbUser(), getDbPass());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        LOGGER.info("Opened database successfully");
    }

    public void buildData(LocalDate from, LocalDate to) {
        ObservableList<ObservableList> data = FXCollections.observableArrayList();

        try {
            if (connection == null) connect();

            String query = "select * from weather";
            statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            if (tableView.getColumns().size() == 0) { //init tableView

                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    //We are using non property style for making dynamic table
                    final int j = i;
                    TableColumn tableColumn = new TableColumn(resultSet.getMetaData().getColumnName(i + 1));
                    //noinspection unchecked
                    tableColumn.setCellValueFactory(param -> {
                        @SuppressWarnings("unchecked") Object property = ((TableColumn.CellDataFeatures<ObservableList, String>) param).getValue().get(j);
                        return new SimpleStringProperty(property != null ? property.toString() : "null");
                    });

                    tableView.getColumns().addAll(tableColumn);
                    LOGGER.debug("Column [{}] ", i);
                }
            }

            // Data added to ObservableList
            int cnt = 1;
            while (resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    row.add(resultSet.getString(i));
                }
                LOGGER.debug("Row [{}] added {}", cnt++, row);
                data.add(row);
            }

            //FINALLY ADDED TO TableView
            tableView.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error on Building Data");
        }
    }

    private static class LazyHandler {
        private static WeatherConnector instance = new WeatherConnector();
    }
}
