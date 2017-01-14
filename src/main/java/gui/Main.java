package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main extends Application {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        Parent root = loader.load();
        Controller ctrl = loader.getController();

        primaryStage.setOnCloseRequest(ev -> {
            try {
                ctrl.deinitialize();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        primaryStage.setTitle("Projekt lawinowy");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
