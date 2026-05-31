package DashboardLayout.PetDetailsCard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import models.petModels.Pet.Pet;

public class PetDetailsCard extends StackPane {
    private static final String ORANGE = "#DF8456";
    private static final String LIGHT_BORDER = "#E9E1DB";
    private static final String TEXT = "#3A3A3A";
        private static final String WARM_TEXT = "#8A6B57";
    private static final String GREEN = "#58A66A";

        public PetDetailsCard(Pet pet, Runnable onBack, Runnable onAdopt) {
        Pet safePet = pet == null ? createFallbackPet() : pet;

        setMaxWidth(980);
        setMaxHeight(540);
        setPadding(new Insets(0));
        setStyle(
                "-fx-background-color: white;"
                        + " -fx-background-radius: 20;"
                        + " -fx-border-radius: 20;"
                        + " -fx-border-color: " + LIGHT_BORDER + ";"
                        + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 22, 0.16, 0, 3);");

        VBox card = new VBox(0);
        card.setFillWidth(true);

        HBox topRow = new HBox();
        topRow.setPadding(new Insets(16, 24, 16, 24));
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.setStyle("-fx-border-color: transparent transparent " + LIGHT_BORDER + " transparent; -fx-border-width: 0 0 1 0;");

        Button backButton = new Button("\u2190 Back to Browse");
        backButton.setOnAction(event -> {
            if (onBack != null) {
                onBack.run();
            }
        });
        backButton.setStyle(
                "-fx-background-color: transparent;"
                        + " -fx-text-fill: " + WARM_TEXT + ";"
                        + " -fx-font-size: 13px;"
                        + " -fx-font-weight: normal;"
                        + " -fx-padding: 0;"
                        + " -fx-cursor: hand;");

        topRow.getChildren().add(backButton);

        HBox body = new HBox(36);
        body.setAlignment(Pos.CENTER_LEFT);
        body.setPadding(new Insets(28, 36, 34, 36));

        StackPane imagePane = new StackPane();
        imagePane.setPrefSize(280, 320);
        imagePane.setMinSize(280, 320);
        imagePane.setMaxSize(280, 320);
        imagePane.setStyle("-fx-background-color: #F7F2EE; -fx-background-radius: 18;");

        Circle circle = new Circle(88);
        circle.setStyle("-fx-fill: #F2E3DB; -fx-stroke: " + ORANGE + "; -fx-stroke-width: 3;");

        Label avatarLabel = new Label(getAvatarLetter(safePet.getName()));
        avatarLabel.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: " + ORANGE + ";");

        StackPane avatarStack = new StackPane(circle, avatarLabel);
        imagePane.getChildren().add(avatarStack);

        VBox details = new VBox(14);
        details.setAlignment(Pos.TOP_LEFT);
        details.setFillWidth(true);
        details.setMaxWidth(Double.MAX_VALUE);

        Label nameLabel = new Label(safePet.getName().isBlank() ? "Buddy" : safePet.getName());
        nameLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + TEXT + ";");

        Label breedLabel = new Label(safeText(safePet.getBreed(), "Golden Retriever"));
        breedLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B6B6B;");

        Label ageLabel = new Label("AGE");
        ageLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #9A7D6E; -fx-font-weight: bold;");

        Label ageValue = new Label(safePet.getAge() <= 0 ? "3 years old" : safePet.getAge() + " years old");
        ageValue.setStyle("-fx-font-size: 18px; -fx-text-fill: " + TEXT + ";");

        Label vaccinationLabel = new Label("VACCINATION STATUS");
        vaccinationLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #9A7D6E; -fx-font-weight: bold;");

        HBox vaccinationRow = new HBox(10);
        vaccinationRow.setAlignment(Pos.CENTER_LEFT);

        Label status = new Label(Boolean.TRUE.equals(safePet.getVaccinated()) ? "Vaccinated" : "Not Vaccinated");
        status.setStyle(
                "-fx-background-color: " + (Boolean.TRUE.equals(safePet.getVaccinated()) ? "#E1F4E6" : "#F2E3DB") + ";"
                        + " -fx-text-fill: " + (Boolean.TRUE.equals(safePet.getVaccinated()) ? GREEN : ORANGE) + ";"
                        + " -fx-background-radius: 14;"
                        + " -fx-padding: 6 14 6 14;"
                        + " -fx-font-size: 13px;");

        vaccinationRow.getChildren().add(status);

        Label descriptionLabel = new Label("DESCRIPTION");
        descriptionLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #9A7D6E; -fx-font-weight: bold;");

        Label description = new Label(safePet.getDescription().isBlank()
                ? "Buddy is a friendly and energetic Golden Retriever who loves fetch, walks, and spending time with people."
                : safePet.getDescription());
        description.setWrapText(true);
        description.setMaxWidth(520);
        description.setStyle("-fx-font-size: 14px; -fx-text-fill: #4E4E4E; -fx-line-spacing: 5;");

        Button adoptButton = new Button("Adopt Me");
        adoptButton.setPrefHeight(52);
        adoptButton.setMaxWidth(Double.MAX_VALUE);
                adoptButton.setOnAction(event -> {
                        if (onAdopt != null) {
                                onAdopt.run();
                        }
                });
        adoptButton.setStyle(
                "-fx-background-color: " + GREEN + ";"
                        + " -fx-text-fill: white;"
                        + " -fx-font-size: 16px;"
                        + " -fx-font-weight: bold;"
                        + " -fx-background-radius: 14;"
                        + " -fx-cursor: hand;");

        details.getChildren().clear();
        details.getChildren().addAll(nameLabel, breedLabel, ageLabel, ageValue, vaccinationLabel, vaccinationRow, descriptionLabel, description, adoptButton);

        HBox.setHgrow(details, javafx.scene.layout.Priority.ALWAYS);
        body.getChildren().addAll(imagePane, details);

        card.getChildren().addAll(topRow, body);
        getChildren().add(card);
    }

    private static Pet createFallbackPet() {
        return new Pet(
                "Buddy",
                3,
                "Golden Retriever",
                "Dog",
                true,
                "Buddy is a friendly and energetic Golden Retriever who loves fetch, walks, and spending time with people.",
                0
        );
    }

        private static String getAvatarLetter(String name) {
                if (name == null || name.isBlank()) {
                        return "?";
                }

                return name.substring(0, 1).toUpperCase();
        }

        private static String safeText(String text, String fallback) {
                return text == null || text.isBlank() ? fallback : text;
        }
}