package LoginLayout;

import LoginLayout.LoginCard.LoginCard;
import LoginLayout.SignUpCard.SignUpCard;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class LoginLayout extends HBox {
    private StackPane rightPane = new StackPane();
    public LoginLayout() {

        // Left Panel 
        VBox leftPane = new VBox(15);
        leftPane.setAlignment(Pos.CENTER_LEFT);
        leftPane.setPadding(new Insets(50));
        leftPane.setStyle("-fx-background-color: #DF8456;");
        leftPane.setMinWidth(0);
        leftPane.setPrefWidth(0);
        leftPane.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(leftPane, Priority.ALWAYS); // Makes it take half the screen

        Label title = new Label("Petopia");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label description = new Label("Find Your Forever Companion.");
        description.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        description.setWrapText(true);

        leftPane.getChildren().addAll(title, description);

        // Right Panel (Gray background, StackPane to center the card)
        rightPane.setStyle("-fx-background-color: #E2E2E2;"); // Light gray
        rightPane.setMinWidth(0);
        rightPane.setPrefWidth(0);
        rightPane.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(rightPane, Priority.ALWAYS);

        rightPane.getChildren().add(new LoginCard(() -> getSignUpCard())); // Start with Login Cardq
        
        // Add both panes to root
        this.getChildren().addAll(leftPane, rightPane);
    }

    public void getLoginCard() {
        LoginCard loginCard = new LoginCard(() -> getSignUpCard());
        rightPane.getChildren().clear();
        rightPane.getChildren().add(loginCard);
    }

    public void getSignUpCard() {
        SignUpCard signUpCard = new SignUpCard(() -> getLoginCard());
        rightPane.getChildren().clear();
        rightPane.getChildren().add(signUpCard);
    }
}