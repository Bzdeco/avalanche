package avalanche;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static final String STAGE_TITLE = "Avalanche Risk Project";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("avalanche.fxml"));
        final Parent root = loader.load();
        primaryStage.setTitle(STAGE_TITLE);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
