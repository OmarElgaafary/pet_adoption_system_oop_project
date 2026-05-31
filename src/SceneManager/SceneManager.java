package SceneManager;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

import CheckoutLayout.CheckoutLayout;
import CheckoutLayout.OrderCompleteLayout;
import DashboardLayout.DashboardLayout;
import LoginLayout.LoginLayout;
import Route.Route;

public class SceneManager {

    private static SceneManager instance;
    private Stage primaryStage;
    
    // Optional: Cache scenes so you don't recreate them every time
    private final Map<Route, Scene> sceneCache = new HashMap<>();

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    // Called exactly ONCE in your Main.java to give the manager the window
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * The single method your components will call to navigate.
     */
    public void switchScene(Route route) {
        Scene scene;

        if (route == Route.DASHBOARD || route == Route.CHECKOUT || route == Route.ORDER_COMPLETE) {
            scene = buildScene(route);
            sceneCache.put(route, scene);
        } else {
            scene = sceneCache.get(route);

            // If the scene hasn't been created yet, build it
            if (scene == null) {
                scene = buildScene(route);
                sceneCache.put(route, scene);
            }
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * A factory method that builds the UI based on the requested route.
     */
    private Scene buildScene(Route route) {
        switch (route) {
            case LOGIN:
                // Return your Login Scene
                return new Scene(new LoginLayout(), 1000, 750);
            case DASHBOARD:
                // Return your Dashboard Scene
                return new Scene(new DashboardLayout(), 1000, 750);
            case CHECKOUT:
                return new Scene(new CheckoutLayout(), 1000, 750);
            case ORDER_COMPLETE:
                return new Scene(new OrderCompleteLayout(), 1000, 750);
            case PET_PROFILE:
                return new Scene(new StackPane(new Label("Pet Profile")), 1000, 750);
            case PET_TRANSACTION:
                return new Scene(new StackPane(new Label("Pet Transaction")), 1000, 750);
            default:
                throw new IllegalArgumentException("Unknown route: " + route);
        }
    }
}