package LoginLayout.LoginCard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import models.exceptionModels.InvalidCredentialsException.InvalidCredentialsException;

import SceneManager.SceneManager;
import UserSession.UserSession;
import Route.Route;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import database.DatabaseManager.DatabaseManager;

public class LoginCard extends VBox {
    public LoginCard(Runnable goToSignUp) {
        this.setMaxSize(400, 450); 
        this.setPadding(new Insets(40));
        this.setStyle("-fx-background-color: white; -fx-background-radius: 15;");

        Label welcomeText = new Label("Welcome Back! ");
        welcomeText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label subText = new Label("Sign in to continue your adoption journey");
        subText.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        GridPane formGrid = new GridPane();
        formGrid.setVgap(15);

        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email...");
        emailField.setStyle("-fx-background-color: #F0F0F0; -fx-background-radius: 20; -fx-padding: 10;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password...");
        passwordField.setStyle("-fx-background-color: #F0F0F0; -fx-background-radius: 20; -fx-padding: 10;");

        // Allows fields to stretch across the grid
        GridPane.setHgrow(emailField, Priority.ALWAYS);
        GridPane.setHgrow(passwordField, Priority.ALWAYS);

        formGrid.add(emailField, 0, 0);
        formGrid.add(passwordField, 0, 1);

        Button signInBtn = new Button("Sign In");
        signInBtn.setMaxWidth(Double.MAX_VALUE); 
        signInBtn.setStyle("-fx-background-color: #639A58; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 10;");
        
        signInBtn.setOnAction(event ->{
            try {
                int userId = handleLogin(emailField.getText(), passwordField.getText());
                UserSession.setUserSession(userId);
            } catch (InvalidCredentialsException e) {
                System.out.println("Login failed: " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText(null);
                alert.setContentText("Invalid email or password. Please try again.");
                alert.showAndWait();
                return;
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login Successful");
            alert.setHeaderText(null);
            alert.setContentText("Welcome back! Redirecting to your dashboard...");
            alert.showAndWait();

            SceneManager.getInstance().switchScene(Route.DASHBOARD);
        });

        Label signUpPrompt = new Label("Don't have an account? Click here to Sign up.");
        signUpPrompt.setStyle("-fx-font-size: 11px;");
        signUpPrompt.setOnMouseClicked(event -> {
            goToSignUp.run();
        });

        this.getChildren().addAll(welcomeText, subText, formGrid, signInBtn, signUpPrompt);
        this.setAlignment(Pos.TOP_CENTER);
    }

    public int handleLogin(String email, String password) throws InvalidCredentialsException {
        Connection conn = DatabaseManager.getInstance().getConnection();

        String sql = "SELECT * FROM users WHERE email_address = '" + email + "' AND password = '" + password + "'";

        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                System.out.println("Login successful for user: " + email);
                return rs.getInt("id");
            } else {
                System.out.println("Login failed for user: " + email);
                throw new InvalidCredentialsException("Invalid email or password.");
            }

        } catch (SQLException e) {
            System.out.println("Database error during login: " + e.getMessage());
        }

        throw new InvalidCredentialsException("Unable to validate login.");
    }
}
