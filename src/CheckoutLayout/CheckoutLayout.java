package CheckoutLayout;

import java.util.Arrays;

import Route.Route;
import SceneManager.SceneManager;
import UserSession.UserSession;
import controllers.CheckoutController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import models.adoptionModels.Adoption.Adoption;
import models.petModels.Pet.Pet;
import models.userModels.Adopter.Adopter;

public class CheckoutLayout extends BorderPane {
	private static final String ORANGE = "#DF8456";
	private static final String LIGHT_GRAY = "#E2E2E2";
	private static final String SOFT_ORANGE_ACTIVE = "#F7B696";
	private static Pet selectedPet;

	private final CheckoutController checkoutController = new CheckoutController();
	private final UserSession currentSession;
	private final Adopter existingAdopter;

	private final TextField phoneField = new TextField();
	private final TextArea addressField = new TextArea();
	private final TextField favouritePetTypeField = new TextField();
	private final TextField accountBalanceField = new TextField();
	private final TextArea previousPetsField = new TextArea();
	private final ToggleGroup paymentToggleGroup = new ToggleGroup();

	public CheckoutLayout() {
		setPrefSize(1000, 750);
		setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		setStyle("-fx-background-color: " + LIGHT_GRAY + ";");

		UserSession session;
		Adopter loadedAdopter = null;

		try {
			session = UserSession.getUserSession();
			loadedAdopter = checkoutController.loadAdopterProfile(session.getUserId());
		} catch (IllegalStateException e) {
			session = null;
		}

		this.currentSession = session;
		this.existingAdopter = loadedAdopter;

		if (currentSession == null) {
			setCenter(buildSessionExpiredView());
			return;
		}

		setTop(buildHeader());
		setCenter(buildCheckoutBody());
	}

	public static void setSelectedPet(Pet pet) {
		selectedPet = pet;
	}

	public static Pet getSelectedPet() {
		return selectedPet;
	}

	public static void clearSelectedPet() {
		selectedPet = null;
	}

	private VBox buildHeader() {
		VBox header = new VBox(8);
		header.setPadding(new Insets(24, 30, 18, 30));
		header.setStyle("-fx-background-color: linear-gradient(to right, #F8F2EC, #F4ECE4);");

		HBox titleRow = new HBox(12);
		titleRow.setAlignment(Pos.CENTER_LEFT);

		Circle iconCircle = new Circle(12);
		iconCircle.setStyle("-fx-fill: " + ORANGE + ";");

		Label icon = new Label("❤");
		icon.setStyle("-fx-font-size: 15px; -fx-text-fill: white; -fx-font-weight: bold;");

		StackPane iconStack = new StackPane(iconCircle, icon);

		Label title = new Label("Finalize Your Adoption");
		title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2F2F2F;");

		titleRow.getChildren().addAll(iconStack, title);

		Label subtitle = new Label("Review your adoption details and complete your checkout.");
		subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #7A6D62;");

		header.getChildren().addAll(titleRow, subtitle);
		return header;
	}

	private ScrollPane buildCheckoutBody() {
		HBox cards = new HBox(22);
		cards.setAlignment(Pos.TOP_CENTER);
		cards.setPadding(new Insets(24, 36, 36, 36));

		VBox petCard = buildPetSummaryCard();
		VBox adopterCard = buildAdopterInfoCard();
		VBox summaryColumn = new VBox(18, buildFeeSummaryCard(), buildPaymentCard(), buildCompleteButton());
		summaryColumn.setAlignment(Pos.TOP_CENTER);

		cards.getChildren().addAll(petCard, adopterCard, summaryColumn);

		ScrollPane scrollPane = new ScrollPane(cards);
		scrollPane.setFitToWidth(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
		return scrollPane;
	}

	private VBox buildPetSummaryCard() {
		Pet pet = selectedPet;
		VBox card = createCard(320);

		Label heading = new Label(pet == null ? "Selected Pet" : safeValue(pet.getName(), "Selected Pet"));
		heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2F2F2F;");

		String breedValue = safeValue(pet == null ? null : pet.getBreed(), "Unknown");
		String ageValue = pet == null || pet.getAge() <= 0 ? "Unknown" : pet.getAge() + " years";
		String genderValue = safeValue(pet == null ? null : pet.getGender(), "Unknown");
		String statusValue = pet != null && Boolean.TRUE.equals(pet.getVaccinated()) ? "Vaccinated" : "Not Vaccinated";

		Label description = new Label(pet == null || pet.getDescription() == null || pet.getDescription().isBlank()
				? "No pet has been selected yet."
				: pet.getDescription());
		description.setWrapText(true);
		description.setStyle("-fx-font-size: 12px; -fx-text-fill: #7A7A7A; -fx-line-spacing: 4;");

		card.getChildren().addAll(heading, createDivider(), createDetailRow("Breed", breedValue), createDetailRow("Age", ageValue), createDetailRow("Gender", genderValue), createDetailRow("Status", statusValue), createDivider(), description);
		return card;
	}

	private VBox buildAdopterInfoCard() {
		VBox card = createCard(350);

		Label heading = new Label("Your Information");
		heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2F2F2F;");

		Label fullName = new Label(currentSession.getFirstName() + " " + currentSession.getLastName());
		fullName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2F2F2F;");

		Label email = new Label(currentSession.getEmailAddress());
		email.setStyle("-fx-font-size: 12px; -fx-text-fill: #7A7A7A;");

		styleField(phoneField, "Phone Number");
		styleField(favouritePetTypeField, "Favorite Pet Type");
		styleField(accountBalanceField, "Account Balance");
		styleTextArea(addressField, "Address");
		styleTextArea(previousPetsField, "Previous Pets");

		addressField.setPrefRowCount(3);
		previousPetsField.setPrefRowCount(3);
		previousPetsField.setEditable(false);
		previousPetsField.setDisable(true);

		if (existingAdopter != null) {
			phoneField.setText(String.valueOf(existingAdopter.getPhoneNumber()));
			addressField.setText(existingAdopter.getAddress());
			favouritePetTypeField.setText(existingAdopter.getFavPetType());
			accountBalanceField.setText(String.valueOf(existingAdopter.getAccountBalance()));
			previousPetsField.setText(renderPreviousPets(existingAdopter.getPreviousPets()));
		} else {
			accountBalanceField.setText("0.0");
			previousPetsField.setText("No previous pets recorded.");
		}

		card.getChildren().addAll(heading, fullName, email, phoneField, addressField, favouritePetTypeField, accountBalanceField, previousPetsField);
		return card;
	}

	private VBox buildFeeSummaryCard() {
		VBox card = createCard(300);

		Label heading = new Label("Fee Summary");
		heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2F2F2F;");

		int adoptionFee = 150;
		int medicalFee = 75;
		int adminFee = 25;
		int total = adoptionFee + medicalFee + adminFee;

		card.getChildren().addAll(
				heading,
				createFeeRow("Adoption Fee", "$" + adoptionFee),
				createFeeRow("Vaccination & Medical", "$" + medicalFee),
				createFeeRow("Admin Fee", "$" + adminFee),
				createDivider(),
				createFeeRow("Total Amount", "$" + total, true));
		return card;
	}

	private VBox buildPaymentCard() {
		VBox card = createCard(300);

		Label heading = new Label("Payment Method");
		heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2F2F2F;");

		RadioButton cardPayment = new RadioButton("Credit / Debit Card");
		RadioButton cashPayment = new RadioButton("Cash Payment");
		cardPayment.setToggleGroup(paymentToggleGroup);
		cashPayment.setToggleGroup(paymentToggleGroup);
		cashPayment.setSelected(true);

		stylePaymentOption(cardPayment, false);
		stylePaymentOption(cashPayment, true);

		paymentToggleGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
			stylePaymentOption(cardPayment, cardPayment.isSelected());
			stylePaymentOption(cashPayment, cashPayment.isSelected());
		});

		card.getChildren().addAll(heading, cardPayment, cashPayment);
		return card;
	}

	private Button buildCompleteButton() {
		Button completeButton = new Button("Complete Adoption");
		completeButton.setMaxWidth(Double.MAX_VALUE);
		completeButton.setPrefHeight(52);
		completeButton.setStyle(
				"-fx-background-color: " + ORANGE + ";"
						+ " -fx-text-fill: white;"
						+ " -fx-font-size: 16px;"
						+ " -fx-font-weight: bold;"
						+ " -fx-background-radius: 16;"
						+ " -fx-cursor: hand;");

		completeButton.setOnAction(event -> handleCheckout());
		return completeButton;
	}

	private void handleCheckout() {
		try {
			Pet pet = selectedPet;
			if (pet == null) {
				showAlert(Alert.AlertType.ERROR, "Missing Pet", "Please select a pet before checking out.");
				return;
			}

			int phoneNumber = Integer.parseInt(phoneField.getText().trim());
			double accountBalance = accountBalanceField.getText().isBlank() ? 0.0 : Double.parseDouble(accountBalanceField.getText().trim());

			Adopter adopter = new Adopter(
					currentSession.getFirstName(),
					currentSession.getLastName(),
					currentSession.getAge(),
					currentSession.getEmailAddress(),
					currentSession.getPassword(),
					phoneNumber,
					addressField.getText().trim(),
					favouritePetTypeField.getText().trim(),
					existingAdopter != null ? existingAdopter.getPreviousPets() : new Pet[0]);
			adopter.setUserId(currentSession.getUserId());
			adopter.setAccountBalance(accountBalance);

			Adoption adoption = checkoutController.completeCheckout(currentSession, pet, adopter);
			OrderCompleteLayout.setCompletionDetails(currentSession, pet, adoption);
			clearSelectedPet();
			SceneManager.getInstance().switchScene(Route.ORDER_COMPLETE);
		} catch (NumberFormatException e) {
			showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid phone number and account balance.");
		} catch (RuntimeException e) {
			showAlert(Alert.AlertType.ERROR, "Checkout Failed", e.getMessage());
		}
	}

	private VBox buildSessionExpiredView() {
		VBox wrapper = new VBox(18);
		wrapper.setAlignment(Pos.CENTER);
		wrapper.setPadding(new Insets(50));

		VBox card = createCard(460);
		card.setAlignment(Pos.CENTER);

		Label title = new Label("Session expired");
		title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2F2F2F;");

		Label message = new Label("Please sign in again before continuing to checkout.");
		message.setWrapText(true);
		message.setStyle("-fx-font-size: 14px; -fx-text-fill: #7A7A7A;");

		Button loginButton = new Button("Go to Login");
		loginButton.setOnAction(event -> SceneManager.getInstance().switchScene(Route.LOGIN));
		loginButton.setStyle(
				"-fx-background-color: " + ORANGE + ";"
						+ " -fx-text-fill: white;"
						+ " -fx-font-size: 15px;"
						+ " -fx-font-weight: bold;"
						+ " -fx-background-radius: 14;"
						+ " -fx-padding: 10 22 10 22;"
						+ " -fx-cursor: hand;");

		card.getChildren().addAll(title, message, loginButton);
		wrapper.getChildren().add(card);
		return wrapper;
	}

	private VBox createCard(double width) {
		VBox card = new VBox(14);
		card.setPrefWidth(width);
		card.setMaxWidth(width);
		card.setPadding(new Insets(22));
		card.setStyle(
				"-fx-background-color: white;"
						+ " -fx-background-radius: 18;"
						+ " -fx-border-radius: 18;"
						+ " -fx-border-color: #E6DDD5;"
						+ " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 18, 0.14, 0, 2);");
		return card;
	}

	private Label createFeeRow(String labelText, String valueText) {
		return createFeeRow(labelText, valueText, false);
	}

	private Label createFeeRow(String labelText, String valueText, boolean bold) {
		Label label = new Label(labelText + "  •  " + valueText);
		label.setStyle("-fx-font-size: " + (bold ? "15px" : "13px") + "; -fx-font-weight: " + (bold ? "bold" : "normal") + "; -fx-text-fill: #2F2F2F;");
		return label;
	}

	private HBox createDetailRow(String labelText, String valueText) {
		HBox row = new HBox();
		row.setAlignment(Pos.CENTER_LEFT);

		Label label = new Label(labelText);
		label.setStyle("-fx-font-size: 13px; -fx-text-fill: #7A7A7A;");

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		Label value = new Label(valueText);
		value.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2F2F2F;");

		row.getChildren().addAll(label, spacer, value);
		return row;
	}

	private VBox createDivider() {
		VBox divider = new VBox();
		divider.setPrefHeight(1);
		divider.setStyle("-fx-background-color: #ECE3DA;");
		return divider;
	}

	private void styleField(TextField field, String promptText) {
		field.setPromptText(promptText);
		field.setStyle("-fx-background-color: #F6F3F0; -fx-background-radius: 14; -fx-padding: 10 12; -fx-border-color: #E5DDD4; -fx-border-radius: 14;");
	}

	private void styleTextArea(TextArea area, String promptText) {
		area.setPromptText(promptText);
		area.setWrapText(true);
		area.setStyle("-fx-background-color: #F6F3F0; -fx-background-radius: 14; -fx-padding: 10 12; -fx-border-color: #E5DDD4; -fx-border-radius: 14;");
	}

	private void stylePaymentOption(RadioButton button, boolean active) {
		button.setMaxWidth(Double.MAX_VALUE);
		button.setStyle(
				"-fx-background-color: " + (active ? SOFT_ORANGE_ACTIVE : "#F4F1EE") + ";"
						+ " -fx-text-fill: #2F2F2F;"
						+ " -fx-background-radius: 14;"
						+ " -fx-border-color: " + (active ? ORANGE : "#E0D8CF") + ";"
						+ " -fx-border-radius: 14;"
						+ " -fx-padding: 12 14 12 14;");
	}

	private String renderPreviousPets(Pet[] previousPets) {
		if (previousPets == null || previousPets.length == 0) {
			return "No previous pets recorded.";
		}

		return Arrays.stream(previousPets)
				.map(pet -> pet.getName() == null || pet.getName().isBlank() ? "Pet" : pet.getName())
				.reduce((left, right) -> left + ", " + right)
				.orElse("No previous pets recorded.");
	}

	private String safeValue(String value, String fallback) {
		return value == null || value.isBlank() ? fallback : value;
	}

	private void showAlert(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
