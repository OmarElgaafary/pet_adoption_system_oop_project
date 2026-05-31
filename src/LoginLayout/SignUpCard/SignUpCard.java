package LoginLayout.SignUpCard;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.DatabaseManager.DatabaseManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.exceptionModels.InvalidCredentialsException.InvalidCredentialsException;
import UserSession.UserSession;

import Route.Route;
import SceneManager.SceneManager;

public class SignUpCard extends VBox {
    public SignUpCard(Runnable goBackToLogin) {
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(40));
        this.setSpacing(16);
        this.setMaxSize(400, 450);
        this.setFillWidth(true);
        this.setStyle("-fx-background-color: white; -fx-background-radius: 18; -fx-border-radius: 18; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 24, 0.2, 0, 6);");

        Label signUpLabel = new Label("Sign Up!");
        signUpLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #111111;");
        signUpLabel.setMaxWidth(Double.MAX_VALUE);
        signUpLabel.setAlignment(Pos.CENTER_LEFT);

        Label signUpDesc = new Label("Create your account to start adopting");
        signUpDesc.setStyle("-fx-font-size: 11px; -fx-text-fill: #555555;");
        signUpDesc.setMaxWidth(Double.MAX_VALUE);
        signUpDesc.setAlignment(Pos.CENTER_LEFT);

        VBox grayBox = new VBox(12);
        grayBox.setPadding(new Insets(22));
        grayBox.setAlignment(Pos.CENTER);
        grayBox.setFillWidth(true);
        grayBox.setMaxWidth(Double.MAX_VALUE);
        grayBox.setStyle("-fx-background-color: #D9D9D9; -fx-background-radius: 16;");

        HBox userFirstAndLastName = new HBox(10);
        userFirstAndLastName.setAlignment(Pos.CENTER);
        userFirstAndLastName.setMaxWidth(Double.MAX_VALUE);

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");

        styleField(firstNameField);
        styleField(lastNameField);
        HBox.setHgrow(firstNameField, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(lastNameField, javafx.scene.layout.Priority.ALWAYS);

        userFirstAndLastName.getChildren().addAll(firstNameField, lastNameField);

        TextField ageField = new TextField();
        ageField.setPromptText("Age");
        styleField(ageField);
        ageField.setMaxWidth(Double.MAX_VALUE);
        
        ComboBox<String> favPetSelection = new ComboBox<>();
        favPetSelection.setPromptText("Favorite Pet");
        favPetSelection.getItems().addAll("Dog", "Cat", "Hamster", "Fish", "Birds", "Rabbits");
        favPetSelection.setMaxWidth(Double.MAX_VALUE);
        favPetSelection.setStyle("-fx-background-color: white; -fx-background-radius: 18; -fx-padding: 8 12; -fx-border-color: transparent;");

        TextField emailAddressField = new TextField();
        emailAddressField.setPromptText("Email Address");
        styleField(emailAddressField);
        emailAddressField.setMaxWidth(Double.MAX_VALUE);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleField(passwordField);
        passwordField.setMaxWidth(Double.MAX_VALUE);

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        styleField(confirmPasswordField);
        confirmPasswordField.setMaxWidth(Double.MAX_VALUE);

        Button signUpButton = new Button("Sign Up");
        signUpButton.setMaxWidth(Double.MAX_VALUE);
        signUpButton.setStyle("-fx-background-color: #5FA24C; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 18; -fx-padding: 10 24;");

        signUpButton.setOnAction(event -> {
            if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || ageField.getText().isEmpty() || favPetSelection.getValue() == null || emailAddressField.getText().isEmpty() || passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Missing Information");
                alert.setHeaderText(null);
                alert.setContentText("Please fill in all fields to sign up.");
                alert.showAndWait();
                return;
            }

            try {
                int age = Integer.parseInt(ageField.getText());
                handleSignUp(firstNameField.getText(), lastNameField.getText(), age, favPetSelection.getValue(), emailAddressField.getText(), passwordField.getText());
            } catch (NumberFormatException e) {
                System.out.println("Invalid age input: " + ageField.getText());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Age");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a valid number for age.");
                alert.showAndWait();
            } catch (InvalidCredentialsException e) {
                System.out.println("Sign up failed: " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Sign Up Failed");
                alert.setHeaderText(null);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });

        Label signInPrompt = new Label("Already have an account? Sign In");
        signInPrompt.setStyle("-fx-font-size: 11px; -fx-text-fill: #222222;");
        signInPrompt.setMaxWidth(Double.MAX_VALUE);
        signInPrompt.setAlignment(Pos.CENTER);

        signInPrompt.setOnMouseClicked(event -> {
            goBackToLogin.run();
        });

        grayBox.getChildren().addAll(userFirstAndLastName, ageField, favPetSelection, emailAddressField, passwordField, confirmPasswordField, signUpButton);
        this.getChildren().addAll(signUpLabel, signUpDesc, grayBox, signInPrompt);
    }

    private void styleField(TextField field) {
        field.setMaxWidth(Double.MAX_VALUE);
        field.setStyle("-fx-background-color: white; -fx-background-radius: 18; -fx-padding: 9 12; -fx-border-color: transparent;");
    }

    public void handleSignUp(String firstName, String lastName, int age, String favPet, String email, String password) throws InvalidCredentialsException {
        Connection conn = DatabaseManager.getInstance().getConnection();

        String sql = "SELECT * FROM users WHERE email_address = '" + email + "'";

        // Check if email already exists
        try (java.sql.Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                System.out.println("Sign up failed: Email already in use: " + email);
                throw new InvalidCredentialsException("Email already in use. Please use a different email.");
            } else {
                System.out.println("Sign up successful for email: " + email);
            }


        } catch (SQLException e) {
            System.out.println("Database error during sign up: " + e.getMessage());
        }

        sql = "INSERT INTO users (first_name, last_name, age, email_address, password) VALUES ('"
                + firstName + "', '"
                + lastName + "', "
                + age + ", '"
                + email + "', '"
                + password + "')";

        int userId = -1;

        try (java.sql.Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    userId = keys.getInt(1);
                }
            }
            System.out.println("User registered successfully: " + email);

        } catch (SQLException e) {
            throw new RuntimeException("Database error during user registration: " + e.getMessage(), e);
        }

        if (userId > 0) {
            UserSession.setUserSession(userId);
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sign Up Successful");
        alert.setHeaderText(null);
        alert.setContentText("Your account has been created successfully! Please log in.");
        alert.showAndWait();

        SceneManager.getInstance().switchScene(Route.DASHBOARD);
    }
}
