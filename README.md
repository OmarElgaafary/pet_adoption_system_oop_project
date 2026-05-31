# Pet Adoption System

## 1. Project Overview & Architecture

The Pet Adoption System is a desktop adoption platform built with JavaFX and SQLite. It supports the full lifecycle of a shelter-style adoption flow: user sign-up, login, browsing pets, viewing pet details, completing an adoption checkout, and confirming the order on a success screen. The application also includes a dedicated **My Pets** view that shows pets already adopted by the currently logged-in user.

The project is organized around the **Model-View-Controller (MVC)** pattern:

- **Model** classes under `models/` represent the business entities: users, adopters, pets, pet subtypes, and adoptions.
- **View** classes under `src/` implement the JavaFX UI. The current version uses code-defined JavaFX layouts rather than separate `.fxml` files, which keeps navigation and state handling explicit in code.
- **Controller** classes under `controllers/` encapsulate database access, transaction logic, validation, and record mapping.

The presentation layer communicates with persistence through JDBC and a singleton database manager. In practice, JavaFX screens call controller methods, controllers execute SQL against SQLite, and the results are mapped back into strongly typed model objects. This keeps UI concerns, business logic, and persistence logic separated while still allowing the screens to remain highly dynamic.

### Primary user-facing modules

| Module | Purpose |
|---|---|
| Login | Authenticates users and initializes the active session |
| Dashboard | Displays available pets and their detail cards |
| Pet Details | Expands a selected pet into a focused profile view |
| Checkout | Captures adopter data and completes the adoption transaction |
| Order Completion | Confirms the adoption and provides navigation back to the app |
| My Pets | Queries the adoption history and renders the user’s owned pets |

## 2. Advanced Technical Features 

### Multi-threading and asynchronous processing

The application avoids freezing the JavaFX Application Thread during data loading by using background `Task` execution for grid population.

| Area | Implementation |
|---|---|
| Dashboard pet loading | `DashboardLayout` loads available pets asynchronously before rendering them into a `FlowPane` |
| My Pets loading | `DashboardLayout` uses a background task to fetch the current user’s adopted pets from the database |
| UI responsiveness | Loading states are shown immediately, then replaced on task success or failure |

This is particularly important in JavaFX because database queries and list construction can otherwise block the UI and make the application appear unresponsive.

### Robust exception handling

The codebase uses both custom exceptions and localized runtime safeguards to preserve data integrity and provide user-facing error feedback.

| Layer | Strategy |
|---|---|
| Login and signup | `InvalidCredentialsException` is thrown for invalid or duplicate account operations |
| Checkout flow | `completeCheckout(...)` validates session state, selected pet state, and transaction integrity |
| Database safety | SQLite transaction rollback is used if any step in checkout fails |
| User feedback | JavaFX `Alert` dialogs communicate validation and login/sign-up errors to the user |

Custom exception classes already present in the project include:

- `InvalidCredentialsException`
- `InsufficientFundsException`
- `InvalidPreviousPetException`

### Database design and transaction management

The SQLite schema is normalized into separate tables for users, adopters, pets, pet subtypes, and adoptions. Key relationships are enforced through foreign keys and uniqueness constraints.

| Table | Role |
|---|---|
| `users` | Base account data |
| `adopters` | Adoption profile linked to a user |
| `pets` | Core pet record and adoption status |
| `dogs`, `cats`, `birds` | Type-specific extensions for pet data |
| `adoptions` | Transaction history linking adopters to pets |

Important relational constraints include:

- `adopters.user_id` references `users.id`
- `adoptions.adopter_id` references `adopters.id`
- `adoptions.pet_id` references `pets.id`
- `adoptions.pet_id` is unique, which prevents one pet from being adopted more than once
- `pets.adopted` stores the current availability state used by the dashboard queries

The checkout flow is implemented as a single SQL transaction. The application:

1. Loads or creates the adopter row.
2. Inserts the adoption record.
3. Updates the selected pet to `adopted = 1`.
4. Commits the transaction only if all three steps succeed.
5. Rolls back automatically if any step fails.

That transaction boundary is one of the strongest engineering points in the project because it guarantees the database cannot end up in a half-complete adoption state.

### Beyond the course scope

The project includes several implementation details that go beyond a minimal classroom demo:

- Custom JavaFX components such as `PetCard`, `PetDetailsCard`, `LoginCard`, and `SignUpCard`
- Scene orchestration through `SceneManager` and `Route` instead of hard-coded navigation
- Singleton session management through `UserSession`
- Dynamic scene rebuilding for stateful pages so stale user data is not reused after logout/login
- Inline theme-driven UI styling that keeps the application visually consistent across screens
- A fully local, self-initializing SQLite persistence layer
- A data-driven My Pets view backed by the same schema used for checkout

## 3. Prerequisites & Dependencies

| Requirement | Version / Details |
|---|---|
| Java Development Kit | JDK 24 |
| JavaFX SDK | 26.0.1 |
| JavaFX modules used | `javafx.controls`, `javafx.fxml`, `javafx.graphics` |
| SQLite JDBC driver | `sqlite-jdbc-3.53.1.0.jar` |
| Build tool | None. This project uses raw IDE/library-path setup rather than Maven or Gradle. |

Additional notes:

- The current workspace is configured against `C:\path\to\javafx-sdk-26.0.1\lib`.
- No JSON parser, logging framework, or CSS framework is required.
- The UI styling is authored directly in JavaFX node styles.

## 4. Step-by-Step Build and Setup Process

### Repository cloning

```bash
git https://github.com/OmarElgaafary/pet_adoption_system_oop_project
cd pet_adoption_system_oop_project
```

### Database initialisation and seeding

The SQLite database file is named `petopia.db` and is stored in the project root.

- If `petopia.db` already exists, the application connects to it directly.
- If it does not exist, `DatabaseManager` creates it automatically on startup.
- Table creation is handled in code by `database/DatabaseManager/DatabaseManager.java`; no external `.sql` migration file is required.

The checked-in database is pre-populated with sample users, adopters, pets, and adoptions so a grader can test the application immediately. If you delete the file and relaunch the app, SQLite will recreate an empty schema, but the sample data will no longer be present unless you re-import it.

### Compiling and running

Because the project does not use Maven or Gradle, it is run using raw library paths.

#### Compile from PowerShell

```powershell
javac -sourcepath "src;." -cp ".;C:\path\to\javafx-sdk-26.0.1\lib\javafx.controls.jar;C:\path\to\javafx-sdk-26.0.1\lib\javafx.graphics.jar;C:\path\to\javafx-sdk-26.0.1\lib\javafx.fxml.jar;C:\path\to\sqlite-jdbc.jar" -d bin src\MainApp\MainApp.java
```

#### Run the application from PowerShell

```powershell
java --module-path "C:\path\to\javafx-sdk-26.0.1\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp "bin;C:\path\to\sqlite-jdbc.jar" MainApp.MainApp
```

#### Debug-friendly launch target

The repository also includes a wrapper entry point for IDE debugging:

```powershell
java --module-path "C:\path\to\javafx-sdk-26.0.1\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp "bin;C:\path\to\sqlite-jdbc.jar" Launcher.Launcher
```

In VS Code, the workspace launch configuration already defines `MainApp`, `Launcher`, and `LoginLayout` launch targets in `.vscode/launch.json`.

## 5. Detailed Project Structure

The current codebase uses JavaFX layouts in code rather than FXML files. The directory structure below reflects where the major application layers live.

```text
pet_adoption_system_oop_project/
├─ controllers/
│  ├─ AdopterController.java
│  ├─ BirdController.java
│  ├─ CatController.java
│  ├─ CheckoutController.java
│  ├─ DogController.java
│  ├─ PetController.java
│  └─ UserController.java
├─ database/
│  └─ DatabaseManager/
│     └─ DatabaseManager.java
├─ models/
│  ├─ adoptionModels/
│  │  └─ Adoption/
│  │     └─ Adoption.java
│  ├─ exceptionModels/
│  │  ├─ InsufficientFundsException/
│  │  │  └─ InsufficientFundsException.java
│  │  ├─ InvalidCredentialsException/
│  │  │  └─ InvalidCredentialsException.java
│  │  └─ InvalidPreviousPetException/
│  │     └─ InvalidPreviousPetException.java
│  ├─ petModels/
│  │  ├─ Bird/
│  │  │  └─ Bird.java
│  │  ├─ Cat/
│  │  │  └─ Cat.java
│  │  ├─ Dog/
│  │  │  └─ Dog.java
│  │  └─ Pet/
│  │     └─ Pet.java
│  └─ userModels/
│     ├─ Adopter/
│     │  └─ Adopter.java
│     └─ User/
│        └─ User.java
├─ src/
│  ├─ CheckoutLayout/
│  │  ├─ CheckoutLayout.java
│  │  └─ OrderCompleteLayout.java
│  ├─ DashboardLayout/
│  │  ├─ DashboardLayout.java
│  │  ├─ PetCards/
│  │  │  └─ PetCard.java
│  │  └─ PetDetailsCard/
│  │     └─ PetDetailsCard.java
│  ├─ Launcher/
│  │  └─ Launcher.java
│  ├─ LoginLayout/
│  │  ├─ LoginLayout.java
│  │  ├─ LoginCard/
│  │  │  └─ LoginCard.java
│  │  └─ SignUpCard/
│  │     └─ SignUpCard.java
│  ├─ MainApp/
│  │  └─ MainApp.java
│  ├─ Route/
│  │  └─ Route.java
│  ├─ SceneManager/
│  │  └─ SceneManager.java
│  └─ UserSession/
│     └─ UserSession.java
├─ lib/
├─ petopia.db
└─ README.md
```

### Package responsibilities

| Area | Responsibility |
|---|---|
| `src/` | JavaFX screens, navigation, and custom UI composition |
| `controllers/` | SQL queries, CRUD operations, and transactional business logic |
| `models/` | Domain objects and custom exception types |
| `database/` | SQLite connection singleton and schema bootstrap |
| `petopia.db` | Bundled local database with sample records |

## 6. Testing & Sample Credentials

### Quick-start testing guide for a grader or TA

1. Launch the application and sign in with one of the bundled test accounts listed below.
2. On the Dashboard, verify that the pet grid renders with the current theme and the detail cards open correctly.
3. Click a pet’s **Meet** button to open the pet detail view.
4. Continue to checkout to verify adopter profile capture and transaction completion.
5. Confirm that the order completion screen appears and that the pet is marked as adopted in the database.
6. Open **My Pets** to confirm the adopted pet appears in the owner grid.
7. Log out and log in again to confirm the session resets cleanly and the dashboard does not reuse stale user data.

### Bundled sample credentials

The repository includes a seeded `petopia.db` with test users. The project is adopter-focused and does **not** implement a separate admin role, so the most useful credentials are adopter test accounts.

| Purpose | Email | Password | Notes |
|---|---|---|---|
| Existing adopter with ownership history | `adopter1@example.com` | `password1` | Good for verifying My Pets and logout/session reset |
| Existing user without an adopter profile | `user1@example.com` | `password2` | Good for testing checkout flow creation from a fresh login |
| Existing adopter | `adopter2@example.com` | `password3` | Useful for dashboard and adoption state verification |
| Existing adopter | `adopter3@example.com` | `password4` | Useful for repeatable login/logout checks |

### Sample data available in the bundled database

The checked-in database also contains available pets for checkout testing, including:

- Daisy
- Coco
- Pip
- Nala

If you want to reset the application to a clean schema, delete `petopia.db` and start the app again. Keep in mind that this removes the bundled test data.
