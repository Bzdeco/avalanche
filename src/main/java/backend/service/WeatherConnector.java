package backend.service;

import backend.Utils.Dirs;
import backend.Utils.Util;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
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

    public WeatherConnector(TableView table) {
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
//for tests
//        from = LocalDate.of(2016,12,01);
//        to = LocalDate.of(2016,12,30);

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
            stmt.setDate((int) 1, Date.valueOf(from));
            stmt.setDate((int) 2, Date.valueOf(to));

            ResultSet rs = stmt.executeQuery();

            if (tableView.getColumns().size() == 0) {//init table

//TABLE COLUMN ADDED DYNAMICALLY
                for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                    //We are using non property style for making dynamic table
                    final int j = i;
                    TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                    col.setCellValueFactory(
                            new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                                    Object prop = param.getValue().get(j);
                                    if (j == 6) {
                                        Short windDirEnum = Util.toShort(prop.toString());
                                        return new SimpleStringProperty(prop != null && windDirEnum != null ?
                                                (Dirs.values()[windDirEnum]).toString() : "null");
                                    }
                                    return new SimpleStringProperty(prop != null ? prop.toString() : "null");
                                }
                            });

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
            tableView.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error on Building Data");
        }

    }
}
