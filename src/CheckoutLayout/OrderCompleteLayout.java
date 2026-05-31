package CheckoutLayout;

import Route.Route;
import SceneManager.SceneManager;
import UserSession.UserSession;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class OrderCompleteLayout extends BorderPane {
    private static final String ORANGE = "#DF8456";
    private static final String LIGHT_GRAY = "#E2E2E2";
    private static String adopterName = "";
    private static String petName = "";
    private static String adoptionTime = "";

    public OrderCompleteLayout() {
        setPrefSize(1000, 750);
        setStyle("-fx-background-color: " + LIGHT_GRAY + ";");

        setCenter(buildContent());
    }

    public static void setCompletionDetails(UserSession session, models.petModels.Pet.Pet pet, models.adoptionModels.Adoption.Adoption adoption) {
        adopterName = session == null ? "Adopter" : session.getFirstName();
        petName = pet == null || pet.getName() == null || pet.getName().isBlank() ? "your pet" : pet.getName();
        adoptionTime = adoption == null ? "" : adoption.getAdoptedAt();
    }

    private VBox buildContent() {
        VBox wrapper = new VBox(22);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(40));

        VBox card = new VBox(18);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(560);
        card.setPadding(new Insets(36));
        card.setStyle(
                "-fx-background-color: white;"
                        + " -fx-background-radius: 20;"
                        + " -fx-border-radius: 20;"
                        + " -fx-border-color: #E6DDD5;"
                        + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 22, 0.16, 0, 2);");

        Circle successCircle = new Circle(42);
        successCircle.setStyle("-fx-fill: #5FA24C;");

        Label checkmark = new Label("✓");
        checkmark.setStyle("-fx-font-size: 34px; -fx-text-fill: white; -fx-font-weight: bold;");

        StackPane iconStack = new StackPane(successCircle, checkmark);

        Label title = new Label("Order Complete");
        title.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #2F2F2F;");

        Label message = new Label((adopterName == null || adopterName.isBlank() ? "Your" : adopterName + "'s") + " adoption for " + petName + " was completed successfully.");
        message.setWrapText(true);
        message.setMaxWidth(420);
        message.setStyle("-fx-font-size: 15px; -fx-text-fill: #7A7A7A; -fx-alignment: center;");

        Label info = new Label(adoptionTime == null || adoptionTime.isBlank() ? "Thank you for adopting through Petopia." : "Completed at " + adoptionTime.replace('T', ' '));
        info.setStyle("-fx-font-size: 12px; -fx-text-fill: #9A8A7B;");

        HBox buttonRow = new HBox(14);
        buttonRow.setAlignment(Pos.CENTER);

        Button homeButton = new Button("Home");
        homeButton.setOnAction(event -> SceneManager.getInstance().switchScene(Route.DASHBOARD));

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(event -> {
            CheckoutLayout.clearSelectedPet();
            UserSession.clearSession();
            SceneManager.getInstance().switchScene(Route.LOGIN);
        });

        stylePrimaryButton(homeButton);
        styleSecondaryButton(logoutButton);

        buttonRow.getChildren().addAll(homeButton, logoutButton);

        card.getChildren().addAll(iconStack, title, message, info, buttonRow);
        wrapper.getChildren().add(card);
        return wrapper;
    }

    private void stylePrimaryButton(Button button) {
        button.setPrefHeight(44);
        button.setStyle(
                "-fx-background-color: " + ORANGE + ";"
                        + " -fx-text-fill: white;"
                        + " -fx-font-size: 14px;"
                        + " -fx-font-weight: bold;"
                        + " -fx-background-radius: 14;"
                        + " -fx-padding: 10 22 10 22;"
                        + " -fx-cursor: hand;");
    }

    private void styleSecondaryButton(Button button) {
        button.setPrefHeight(44);
        button.setStyle(
                "-fx-background-color: transparent;"
                        + " -fx-text-fill: #7A6D62;"
                        + " -fx-font-size: 14px;"
                        + " -fx-font-weight: bold;"
                        + " -fx-background-radius: 14;"
                        + " -fx-padding: 10 22 10 22;"
                        + " -fx-border-color: #D8CFC6;"
                        + " -fx-border-radius: 14;"
                        + " -fx-cursor: hand;");
    }
}