package org.avalanche;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AvalancheApplication extends Application {

    private static final String STAGE_TITLE = "Avalanche Risk Project";

    private ConfigurableApplicationContext appContext;
    private FXMLLoader fxmlLoader;
    private Parent fxmlRootNode;

    public static void main(String[] args) {
        // start JavaFx application
        launch(args);
    }

    @Override
    public void init() {
        appContext = SpringApplication.run(AvalancheApplication.class);
        fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("avalanche.fxml"));
        fxmlLoader.setControllerFactory(appContext::getBean);
    }

    @Override
    public void start(Stage stage) throws Exception {
        fxmlRootNode = fxmlLoader.load();
        stage.setTitle(STAGE_TITLE);
        stage.setScene(new Scene(fxmlRootNode));
        stage.show();
    }
}
