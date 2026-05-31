package MainApp;

import javafx.application.Application;
import javafx.stage.Stage;

import SceneManager.SceneManager;
import Route.Route;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) { 

        SceneManager sceneManager = SceneManager.getInstance();

        primaryStage.setTitle("Pet Adoption System");
        sceneManager.setPrimaryStage(primaryStage);
        sceneManager.switchScene(Route.LOGIN);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
