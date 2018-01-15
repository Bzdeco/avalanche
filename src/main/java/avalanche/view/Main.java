package avalanche.view;

import avalanche.controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main extends Application {

    private static final String STAGE_TITLE = "Avalanche Risk Project";
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        final Parent root = loader.load();
        final Controller ctrl = loader.getController();
        /*primaryStage.setOnCloseRequest(ev -> {
            try {
                ctrl.shutdown();
            } catch (InterruptedException e) {
                LOGGER.error("Shutdown interrupted", e);
            }
        });*/
        primaryStage.setTitle(STAGE_TITLE);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
