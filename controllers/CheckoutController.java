package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import UserSession.UserSession;
import database.DatabaseManager.DatabaseManager;
import models.adoptionModels.Adoption.Adoption;
import models.petModels.Pet.Pet;
import models.userModels.Adopter.Adopter;

public class CheckoutController {
    private final Connection connection;
    private final PetController petController;

    public CheckoutController() {
        this.connection = DatabaseManager.getInstance().getConnection();
        this.petController = new PetController();
    }

    public Adopter loadAdopterProfile(int userId) {
        String sql = "SELECT a.id AS adopter_id, a.user_id, u.first_name, u.last_name, u.age, u.email_address, u.password, "
                + "a.phone_number, a.address, a.fav_pet_type, a.previous_pets, a.account_balance "
                + "FROM adopters a JOIN users u ON a.user_id = u.id WHERE a.user_id = " + userId;

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return mapAdopter(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load adopter profile: " + e.getMessage(), e);
        }

        return null;
    }

    public List<Pet> getAdoptedPetsForUser(int userId) {
        String sql = "SELECT p.id AS pet_id FROM adoptions a "
                + "JOIN adopters d ON a.adopter_id = d.id "
                + "JOIN pets p ON a.pet_id = p.id "
                + "WHERE d.user_id = " + userId + " ORDER BY a.adopted_at DESC, p.id DESC";

        List<Pet> pets = new ArrayList<>();

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Pet pet = petController.getPetById(resultSet.getInt("pet_id"));
                if (pet != null) {
                    pets.add(pet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load adopted pets: " + e.getMessage(), e);
        }

        return pets;
    }

    public Adoption completeCheckout(UserSession session, Pet selectedPet, Adopter formAdopter) throws IllegalStateException, IllegalArgumentException {
        if (session == null) {
            throw new IllegalStateException("No active user session found.");
        }

        if (selectedPet == null || selectedPet.getPetId() <= 0) {
            throw new IllegalArgumentException("No pet selected for checkout.");
        }

        boolean originalAutoCommit = true;

        try {
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            Pet livePet = petController.getPetById(selectedPet.getPetId());
            if (livePet == null) {
                throw new IllegalStateException("Selected pet could not be found.");
            }

            if (livePet.getAdopted() == 1) {
                throw new IllegalStateException("This pet has already been adopted.");
            }

            int adopterId = upsertAdopterProfile(session, formAdopter, selectedPet.getPetId());
            int adoptionId = insertAdoption(adopterId, selectedPet.getPetId());
            markPetAdopted(selectedPet.getPetId());

            connection.commit();

            Adoption adoption = new Adoption(adopterId, selectedPet.getPetId(), LocalDateTime.now().toString());
            adoption.setId(adoptionId);
            return adoption;
        } catch (SQLException | RuntimeException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Failed to rollback checkout transaction: " + rollbackException.getMessage(), rollbackException);
            }
            throw new RuntimeException("Failed to complete checkout: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to restore auto-commit mode: " + e.getMessage(), e);
            }
        }
    }

    private int upsertAdopterProfile(UserSession session, Adopter formAdopter, int petId) throws SQLException {
        int adopterId = getAdopterIdByUserId(session.getUserId());
        String previousPetsValue = loadPreviousPetsValue(adopterId);
        String updatedPreviousPets = appendPetId(previousPetsValue, petId);

        if (adopterId == -1) {
            String insertSql = "INSERT INTO adopters (user_id, phone_number, address, fav_pet_type, previous_pets, account_balance) VALUES ("
                    + session.getUserId() + ", "
                    + formAdopter.getPhoneNumber() + ", '"
                    + escapeSql(formAdopter.getAddress()) + "', '"
                    + escapeSql(formAdopter.getFavPetType()) + "', '"
                    + escapeSql(updatedPreviousPets) + "', "
                    + formAdopter.getAccountBalance() + ")";

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(insertSql, Statement.RETURN_GENERATED_KEYS);
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }

            throw new SQLException("Failed to create adopter profile.");
        }

        String updateSql = "UPDATE adopters SET phone_number = "
                + formAdopter.getPhoneNumber() + ", address = '"
                + escapeSql(formAdopter.getAddress()) + "', fav_pet_type = '"
                + escapeSql(formAdopter.getFavPetType()) + "', previous_pets = '"
                + escapeSql(updatedPreviousPets) + "', account_balance = "
                + formAdopter.getAccountBalance() + " WHERE id = " + adopterId;

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(updateSql);
        }

        return adopterId;
    }

    private int getAdopterIdByUserId(int userId) throws SQLException {
        String sql = "SELECT id FROM adopters WHERE user_id = " + userId;

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        }

        return -1;
    }

    private int insertAdoption(int adopterId, int petId) throws SQLException {
        String sql = "INSERT INTO adoptions (adopter_id, pet_id) VALUES (" + adopterId + ", " + petId + ")";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException("Failed to create adoption record.");
    }

    private void markPetAdopted(int petId) throws SQLException {
        String sql = "UPDATE pets SET adopted = 1 WHERE id = " + petId;

        try (Statement statement = connection.createStatement()) {
            if (statement.executeUpdate(sql) <= 0) {
                throw new SQLException("Failed to update pet adoption status.");
            }
        }
    }

    private Adopter mapAdopter(ResultSet resultSet) throws SQLException {
        Adopter adopter = new Adopter();
        adopter.setUserId(resultSet.getInt("user_id"));
        adopter.setFirstName(resultSet.getString("first_name"));
        adopter.setLastName(resultSet.getString("last_name"));
        adopter.setAge(resultSet.getInt("age"));
        adopter.setEmailAddress(resultSet.getString("email_address"));
        adopter.setPassword(resultSet.getString("password"));
        adopter.setPhoneNumber(resultSet.getInt("phone_number"));
        adopter.setAddress(resultSet.getString("address"));
        adopter.setFavPetType(resultSet.getString("fav_pet_type"));
        adopter.setPreviousPets(loadPreviousPets(resultSet.getString("previous_pets")));
        adopter.setAccountBalance(resultSet.getDouble("account_balance"));
        return adopter;
    }

    private Pet[] loadPreviousPets(String previousPetsValue) {
        if (previousPetsValue == null || previousPetsValue.isBlank()) {
            return new Pet[0];
        }

        String[] ids = previousPetsValue.split(",");
        List<Pet> pets = new ArrayList<>();

        for (String rawId : ids) {
            String trimmedId = rawId.trim();
            if (trimmedId.isEmpty()) {
                continue;
            }

            try {
                Pet pet = petController.getPetById(Integer.parseInt(trimmedId));
                if (pet != null) {
                    pets.add(pet);
                }
            } catch (NumberFormatException ignored) {
                // Skip malformed entries.
            }
        }

        return pets.toArray(new Pet[0]);
    }

    private String loadPreviousPetsValue(int adopterId) throws SQLException {
        if (adopterId == -1) {
            return "";
        }

        String sql = "SELECT previous_pets FROM adopters WHERE id = " + adopterId;

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                String value = resultSet.getString("previous_pets");
                return value == null ? "" : value;
            }
        }

        return "";
    }

    private String appendPetId(String previousPetsValue, int petId) {
        String normalizedValue = previousPetsValue == null ? "" : previousPetsValue.trim();
        String petIdValue = String.valueOf(petId);

        if (normalizedValue.isBlank()) {
            return petIdValue;
        }

        String[] ids = normalizedValue.split(",");
        for (String rawId : ids) {
            if (petIdValue.equals(rawId.trim())) {
                return normalizedValue;
            }
        }

        return normalizedValue + "," + petIdValue;
    }

    private String escapeSql(String value) {
        return value == null ? "" : value.replace("'", "''");
    }
}