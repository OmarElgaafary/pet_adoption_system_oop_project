package DashboardLayout;

import java.util.List;

import CheckoutLayout.CheckoutLayout;
import DashboardLayout.PetCards.PetCard;
import DashboardLayout.PetDetailsCard.PetDetailsCard;
import Route.Route;
import SceneManager.SceneManager;
import controllers.PetController;
import controllers.CheckoutController;
import UserSession.UserSession;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import models.petModels.Pet.Pet;

public class DashboardLayout extends BorderPane {
    private static final String PRIMARY_ORANGE = "#DF8456";
    private static final String LIGHT_GRAY = "#E2E2E2";
    private static final String BUTTON_ORANGE = "#F3A37D";
    private static final String BUTTON_ORANGE_ACTIVE = "#F7B696";

    private final StackPane contentPane = new StackPane();
    private final Button homeButton = new Button("Home (Dashboard)");
    private final Button myPetsButton = new Button("My Pets");
    private final Button logoutButton = new Button("Logout");

    private final CheckoutController checkoutController = new CheckoutController();
    private UserSession userSession;

    public DashboardLayout() {
        setPrefSize(1000, 750);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setStyle("-fx-background-color: " + LIGHT_GRAY + ";");

        try {
            userSession = UserSession.getUserSession();
        } catch (IllegalStateException e) {
            System.out.println("No active user session found: " + e.getMessage());
        }

        setTop(createTopBar());
        setCenter(contentPane);
        contentPane.setStyle("-fx-background-color: " + LIGHT_GRAY + ";");

        showDashboardView();
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(14, 20, 14, 20));
        topBar.setStyle("-fx-background-color: " + PRIMARY_ORANGE + ";");

        homeButton.setOnAction(event -> showDashboardView());
        myPetsButton.setOnAction(event -> showMyPetsView());
        logoutButton.setOnAction(event -> {
            userSession = null;
            CheckoutLayout.clearSelectedPet();
            UserSession.clearSession();
            SceneManager.getInstance().switchScene(Route.LOGIN);
        });

        applyNavStyle(homeButton, true);
        applyNavStyle(myPetsButton, false);
        logoutButton.setStyle(navButtonBase() + " -fx-background-color: rgba(255,255,255,0.14); -fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(homeButton, myPetsButton, spacer, logoutButton);
        return topBar;
    }

    private String navButtonBase() {
        return "-fx-background-radius: 16;"
                + " -fx-padding: 10 18 10 18;"
                + " -fx-font-size: 14px;"
                + " -fx-font-weight: bold;"
                + " -fx-cursor: hand;";
    }

    private void applyNavStyle(Button button, boolean active) {
        button.setStyle(navButtonBase()
                + " -fx-background-color: " + (active ? BUTTON_ORANGE_ACTIVE : BUTTON_ORANGE) + ";"
                + " -fx-text-fill: white;");
    }

    private void showDashboardView() {
        applyNavStyle(homeButton, true);
        applyNavStyle(myPetsButton, false);
        contentPane.getChildren().setAll(createDashboardContent(null));
    }

    private void showPetDetailsView(Pet pet) {
        applyNavStyle(homeButton, true);
        applyNavStyle(myPetsButton, false);
        contentPane.getChildren().setAll(createDashboardContent(pet));
    }

    private void openCheckout(Pet pet) {
        CheckoutLayout.setSelectedPet(pet);
        SceneManager.getInstance().switchScene(Route.CHECKOUT);
    }

    private void showMyPetsView() {
        applyNavStyle(homeButton, false);
        applyNavStyle(myPetsButton, true);
        contentPane.getChildren().setAll(createMyPetsView());
    }

    private VBox createDashboardContent(Pet selectedPet) {
        VBox dashboardContent = new VBox(22);
        dashboardContent.setAlignment(Pos.TOP_CENTER);
        dashboardContent.setPadding(new Insets(22, 0, 0, 0));
        dashboardContent.setStyle("-fx-background-color: " + LIGHT_GRAY + ";");

        VBox hero = new VBox(12);
        hero.setAlignment(Pos.CENTER);
        hero.setPadding(new Insets(36, 40, 36, 40));
        hero.setMaxWidth(Double.MAX_VALUE);
        hero.setStyle(
            "-fx-background-color: " + PRIMARY_ORANGE + ";"
                        + " -fx-background-radius: 18;"
                        + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 18, 0.14, 0, 2);");
        
        String userFirstName = userSession != null ? userSession.getFirstName() : "Adopter";
        Label title = new Label("Hello, " + userFirstName + "!");
        title.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitle = new Label("Browse pets available for adoption and meet the one that fits your home.");
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(720);
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: rgba(255,255,255,0.92);");

        hero.getChildren().addAll(title, subtitle);

        StackPane bodyPane = new StackPane();
        bodyPane.setPadding(new Insets(0, 40, 40, 40));

        if (selectedPet == null) {
            FlowPane cardGrid = new FlowPane();
            cardGrid.setHgap(28);
            cardGrid.setVgap(28);
            cardGrid.setAlignment(Pos.CENTER);
            cardGrid.setPrefWrapLength(700);
            cardGrid.prefWrapLengthProperty().bind(Bindings.subtract(contentPane.widthProperty(), 120));
            cardGrid.setStyle("-fx-background-color: transparent;");

            Label loadingState = new Label("Loading pets...");
            loadingState.setStyle("-fx-font-size: 16px; -fx-text-fill: #5F6B7A; -fx-padding: 24 0 0 0;");
            cardGrid.getChildren().add(loadingState);
            loadPetsAsync(cardGrid);
            bodyPane.getChildren().add(cardGrid);
        } else {
            bodyPane.getChildren().add(new PetDetailsCard(selectedPet, this::showDashboardView, () -> openCheckout(selectedPet)));
        }

        ScrollPane contentScroll = new ScrollPane(bodyPane);
        contentScroll.setFitToWidth(true);
        contentScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        contentScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        contentScroll.setPannable(true);
        contentScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox contentWrapper = new VBox(contentScroll);
        contentWrapper.setPadding(new Insets(0));
        contentWrapper.setStyle("-fx-background-color: " + LIGHT_GRAY + ";");
        VBox.setVgrow(contentScroll, Priority.ALWAYS);

        dashboardContent.getChildren().addAll(hero, contentWrapper);
        VBox.setVgrow(contentWrapper, Priority.ALWAYS);
        return dashboardContent;
    }

    private VBox createMyPetsView() {
        VBox wrapper = new VBox(22);
        wrapper.setAlignment(Pos.TOP_CENTER);
        wrapper.setPadding(new Insets(22, 0, 0, 0));
        wrapper.setStyle("-fx-background-color: " + LIGHT_GRAY + ";");

        VBox hero = new VBox(10);
        hero.setAlignment(Pos.CENTER);
        hero.setPadding(new Insets(34, 40, 34, 40));
        hero.setMaxWidth(Double.MAX_VALUE);
        hero.setStyle(
                "-fx-background-color: " + PRIMARY_ORANGE + ";"
                        + " -fx-background-radius: 18;"
                        + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 18, 0.14, 0, 2);");

        String userFirstName = userSession != null ? userSession.getFirstName() : "Adopter";
        Label heading = new Label(userFirstName + "'s Pets");
        heading.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label message = new Label("Pets you have already adopted appear below.");
        message.setStyle("-fx-font-size: 13px; -fx-text-fill: rgba(255,255,255,0.92);");

        hero.getChildren().addAll(heading, message);

        StackPane bodyPane = new StackPane();
        bodyPane.setPadding(new Insets(0, 40, 40, 40));

        FlowPane cardGrid = new FlowPane();
        cardGrid.setHgap(28);
        cardGrid.setVgap(28);
        cardGrid.setAlignment(Pos.CENTER);
        cardGrid.setPrefWrapLength(700);
        cardGrid.prefWrapLengthProperty().bind(Bindings.subtract(contentPane.widthProperty(), 120));
        cardGrid.setStyle("-fx-background-color: transparent;");

        Label loadingState = new Label("Loading your pets...");
        loadingState.setStyle("-fx-font-size: 16px; -fx-text-fill: #5F6B7A; -fx-padding: 24 0 0 0;");
        cardGrid.getChildren().add(loadingState);
        loadOwnedPetsAsync(cardGrid);

        bodyPane.getChildren().add(cardGrid);

        ScrollPane contentScroll = new ScrollPane(bodyPane);
        contentScroll.setFitToWidth(true);
        contentScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        contentScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        contentScroll.setPannable(true);
        contentScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox contentWrapper = new VBox(contentScroll);
        contentWrapper.setPadding(new Insets(0));
        contentWrapper.setStyle("-fx-background-color: " + LIGHT_GRAY + ";");
        VBox.setVgrow(contentScroll, Priority.ALWAYS);

        wrapper.getChildren().addAll(hero, contentWrapper);
        VBox.setVgrow(contentWrapper, Priority.ALWAYS);
        return wrapper;
    }

    private void loadOwnedPetsAsync(FlowPane cardGrid) {
        Task<List<Pet>> ownedPetsTask = new Task<>() {
            @Override
            protected List<Pet> call() {
                if (userSession == null) {
                    return List.of();
                }

                return checkoutController.getAdoptedPetsForUser(userSession.getUserId());
            }
        };

        ownedPetsTask.setOnSucceeded(event -> {
            List<Pet> pets = ownedPetsTask.getValue();
            cardGrid.getChildren().clear();

            if (pets == null || pets.isEmpty()) {
                Label emptyState = new Label("You have not adopted any pets yet.");
                emptyState.setStyle("-fx-font-size: 16px; -fx-text-fill: #5F6B7A; -fx-padding: 24 0 0 0;");
                cardGrid.getChildren().add(emptyState);
                return;
            }

            for (Pet pet : pets) {
                cardGrid.getChildren().add(new PetCard(pet, "Adopted", null));
            }
        });

        ownedPetsTask.setOnFailed(event -> {
            cardGrid.getChildren().clear();
            Throwable failure = ownedPetsTask.getException();
            Label errorState = new Label(failure == null ? "Failed to load your pets." : "Failed to load your pets: " + failure.getMessage());
            errorState.setWrapText(true);
            errorState.setStyle("-fx-font-size: 16px; -fx-text-fill: #5F6B7A; -fx-padding: 24 0 0 0;");
            cardGrid.getChildren().add(errorState);
        });

        Thread loaderThread = new Thread(ownedPetsTask, "owned-pet-loader");
        loaderThread.setDaemon(true);
        loaderThread.start();
    }

    private void loadPetsAsync(FlowPane cardGrid) {
        Task<List<Pet>> petLoadTask = new Task<>() {
            @Override
            protected List<Pet> call() {
                return new PetController().getAllPets();
            }
        };

        petLoadTask.setOnSucceeded(event -> {
            List<Pet> pets = petLoadTask.getValue();
            cardGrid.getChildren().clear();

            if (pets == null || pets.isEmpty()) {
                Label emptyState = new Label("No pets available right now.");
                emptyState.setStyle("-fx-font-size: 16px; -fx-text-fill: #5F6B7A; -fx-padding: 24 0 0 0;");
                cardGrid.getChildren().add(emptyState);
                return;
            }

            for (Pet pet : pets) {
                cardGrid.getChildren().add(new PetCard(pet, () -> showPetDetailsView(pet)));
            }
        });

        petLoadTask.setOnFailed(event -> {
            cardGrid.getChildren().clear();
            Throwable failure = petLoadTask.getException();
            Label errorState = new Label(failure == null ? "Failed to load pets." : "Failed to load pets: " + failure.getMessage());
            errorState.setWrapText(true);
            errorState.setStyle("-fx-font-size: 16px; -fx-text-fill: #5F6B7A; -fx-padding: 24 0 0 0;");
            cardGrid.getChildren().add(errorState);
        });

        Thread loaderThread = new Thread(petLoadTask, "pet-loader");
        loaderThread.setDaemon(true);
        loaderThread.start();
    }
}
