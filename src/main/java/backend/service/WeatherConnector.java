package backend.service;

import backend.Utils.Dirs;
import backend.Utils.Util;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;

import static backend.ResourceHandler.*;

public class WeatherConnector {
    private static final Logger logger = LogManager.getLogger();

    private Connection c = null;
    private PreparedStatement stmt = null;
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
            c = DriverManager
                    .getConnection(getDbUrl(), getDbUser(), getDbPass());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        logger.info("Opened database successfully");
    }

    public void buildData(LocalDate from, LocalDate to) {
        ObservableList<ObservableList> data = FXCollections.observableArrayList();

        if (from == null || to == null) {
            logger.info("Choose dates first.");
            return;
        }

        try {
            if (c == null)
                connect();

            String query = "select * " +
                    "from weather " +
                    "where time between ? and ?";
            stmt = c.prepareStatement(query);
            //noinspection JpaQueryApiInspection
            stmt.setDate(1, Date.valueOf(from));
            //noinspection JpaQueryApiInspection
            stmt.setDate(2, Date.valueOf(to));

            ResultSet rs = stmt.executeQuery();

            if (tableView.getColumns().size() == 0) {//init table

//TABLE COLUMN ADDED DYNAMICALLY
                for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                    //We are using non property style for making dynamic table
                    final int j = i;
                    TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                    //noinspection unchecked
                    col.setCellValueFactory(param -> {
                        @SuppressWarnings("unchecked") Object prop = ((TableColumn.CellDataFeatures<ObservableList, String>) param).getValue().get(j);
                        if (j == 6) {
                            Short windDirEnum = Util.toShort(prop.toString());
                            return new SimpleStringProperty(prop != null && windDirEnum != null ?
                                    (Dirs.values()[windDirEnum]).toString() : "null");
                        }
                        return new SimpleStringProperty(prop != null ? prop.toString() : "null");
                    });

                    //noinspection unchecked
                    tableView.getColumns().addAll(col);
                    logger.debug("Column [{}] ", i);
                }
            }

// Data added to ObservableList
            int cnt = 1;
            while (rs.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                logger.debug("Row [{}] added {}", cnt++, row);
                data.add(row);

            }

            //FINALLY ADDED TO TableView
            //noinspection unchecked
            tableView.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error on Building Data");
        }

    }

    private static class LazyHandler{
        private static WeatherConnector instance = new WeatherConnector();
    }
}
