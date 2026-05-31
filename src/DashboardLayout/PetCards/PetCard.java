package DashboardLayout.PetCards;

import models.petModels.Pet.Pet;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class PetCard extends StackPane {
    private static final double CARD_WIDTH = 210;
    private static final double CARD_HEIGHT = 250;
        private static final String GREEN = "#58A66A";

    public PetCard(String name, String breed, String avatarLetter, String accentColor) {
                this(name, breed, "", avatarLetter, accentColor, "Meet", null);
        }

        public PetCard(Pet pet) {
                this(pet, null);
        }

        public PetCard(Pet pet, Runnable onMeetClick) {
                this(
                                pet.getName(),
                                pet.getBreed(),
                                pet.getDescription(),
                                getAvatarLetter(pet.getName()),
                                getAccentColor(pet.getName(), pet.getBreed()),
                                "Meet " + pet.getName(),
                                onMeetClick);
        }

        public PetCard(Pet pet, String actionLabel, Runnable onAction) {
                this(
                                pet.getName(),
                                pet.getBreed(),
                                pet.getDescription(),
                                getAvatarLetter(pet.getName()),
                                getAccentColor(pet.getName(), pet.getBreed()),
                                actionLabel,
                                onAction);
        }

        private PetCard(String name, String breed, String description, String avatarLetter, String accentColor, String actionLabel, Runnable onAction) {
        setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        setMinSize(200, CARD_HEIGHT);
        setMaxSize(CARD_WIDTH, CARD_HEIGHT);
        setStyle(
                "-fx-background-color: white;"
                        + " -fx-background-radius: 14;"
                        + " -fx-border-radius: 14;"
                        + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 18, 0.16, 0, 2);");

        VBox body = new VBox(8);
        body.setAlignment(Pos.TOP_CENTER);
        body.setPadding(new Insets(30, 18, 22, 18));

        StackPane avatar = new StackPane();
        avatar.setPrefSize(66, 66);
        avatar.setMinSize(66, 66);
        avatar.setMaxSize(66, 66);
        avatar.setStyle("-fx-background-color: white;");

        Circle circle = new Circle(31);
        circle.setStyle("-fx-fill: white; -fx-stroke: #8F6F5C; -fx-stroke-width: 1.1;");

        Label avatarLabel = new Label(avatarLetter);
        avatarLabel.setStyle(
                "-fx-text-fill: " + accentColor + ";"
                        + " -fx-font-size: 22px;"
                        + " -fx-font-weight: bold;");

        avatar.getChildren().addAll(circle, avatarLabel);

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #3A3A3A;");

        Label breedLabel = new Label(breed);
        breedLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7A7A7A;");

        Label descriptionLabel = new Label(description == null || description.isBlank() ? "Ready for adoption." : description);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(160);
        descriptionLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #8A8A8A;");

        Button action = new Button(actionLabel == null || actionLabel.isBlank() ? "Meet " + name : actionLabel);
        action.setMaxWidth(150);
        action.setPrefHeight(28);
        action.setStyle(
                                "-fx-background-color: " + GREEN + ";"
                        + " -fx-text-fill: white;"
                        + " -fx-font-weight: bold;"
                        + " -fx-background-radius: 7;"
                        + " -fx-padding: 0 12 0 12;");
                if (onAction != null) {
                        action.setOnAction(event -> onAction.run());
                }

                body.getChildren().addAll(avatar, nameLabel, breedLabel, descriptionLabel, action);
        getChildren().add(body);
    }

        private static String getAvatarLetter(String name) {
                if (name == null || name.isBlank()) {
                        return "?";
                }

                return name.substring(0, 1).toUpperCase();
        }

        private static String getAccentColor(String name, String breed) {
                String[] palette = {"#DF8456", "#E58B6B", "#E2B04C", "#C97A57", "#D58AA7", "#9C7B60"};
                int hash = 0;
                String seed = (name == null ? "" : name) + (breed == null ? "" : breed);

                for (int index = 0; index < seed.length(); index++) {
                        hash = 31 * hash + seed.charAt(index);
                }

                return palette[Math.abs(hash) % palette.length];
        }
}