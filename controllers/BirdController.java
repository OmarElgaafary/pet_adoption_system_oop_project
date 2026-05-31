package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseManager.DatabaseManager;
import models.petModels.Bird.Bird;

public class BirdController {
    private final Connection connection;
    private final PetController petController;

    public BirdController() {
        this.connection = DatabaseManager.getInstance().getConnection();
        this.petController = new PetController();
    }

    public int createBird(Bird bird) {
        try {
            connection.setAutoCommit(false);

            int petId = petController.createPet(bird, "Bird");
            String sql = "INSERT INTO birds (pet_id, wing_span, can_fly) VALUES ("
                    + petId + ", '" + bird.getWingSpan() + "', "
                    + (bird.canFly() ? 1 : 0) + ")";

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);

                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        connection.commit();
                        return keys.getInt(1);
                    }
                }

                connection.commit();
            }
        } catch (SQLException | RuntimeException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Failed to rollback bird creation: " + rollbackException.getMessage(), rollbackException);
            }
            throw new RuntimeException("Failed to create bird: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to restore auto-commit mode: " + e.getMessage(), e);
            }
        }

        return -1;
    }

    public Bird getBirdById(int birdId) {
        String sql = "SELECT b.id AS bird_id, p.name, p.age, p.breed, p.gender, p.vaccinated, p.description, b.wing_span, b.can_fly " +
                     "FROM birds b JOIN pets p ON b.pet_id = p.id WHERE b.id = " + birdId;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return mapBird(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch bird: " + e.getMessage(), e);
        }

        return null;
    }

    public List<Bird> getAllBirds() {
        List<Bird> birds = new ArrayList<>();
        String sql = "SELECT b.id AS bird_id, p.name, p.age, p.breed, p.gender, p.vaccinated, p.description, b.wing_span, b.can_fly " +
                     "FROM birds b JOIN pets p ON b.pet_id = p.id ORDER BY b.id";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                birds.add(mapBird(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch birds: " + e.getMessage(), e);
        }

        return birds;
    }

    public boolean updateBird(int birdId, Bird bird) {
        try {
            connection.setAutoCommit(false);

            int petId = getPetIdForBird(birdId);
            if (petId == -1) {
                connection.rollback();
                return false;
            }

            boolean petUpdated = petController.updatePet(petId, bird, "Bird");

            String sql = "UPDATE birds SET wing_span = '" + bird.getWingSpan() + "', can_fly = "
                    + (bird.canFly() ? 1 : 0) + " WHERE id = " + birdId;
            try (Statement statement = connection.createStatement()) {

                boolean birdUpdated = statement.executeUpdate(sql) > 0;
                connection.commit();
                return petUpdated && birdUpdated;
            }
        } catch (SQLException | RuntimeException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Failed to rollback bird update: " + rollbackException.getMessage(), rollbackException);
            }
            throw new RuntimeException("Failed to update bird: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to restore auto-commit mode: " + e.getMessage(), e);
            }
        }
    }

    public boolean deleteBird(int birdId) {
        try {
            connection.setAutoCommit(false);

            int petId = getPetIdForBird(birdId);
            if (petId == -1) {
                connection.rollback();
                return false;
            }

            boolean deleted = petController.deletePet(petId);
            connection.commit();
            return deleted;
        } catch (SQLException | RuntimeException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Failed to rollback bird deletion: " + rollbackException.getMessage(), rollbackException);
            }
            throw new RuntimeException("Failed to delete bird: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to restore auto-commit mode: " + e.getMessage(), e);
            }
        }
    }

    private int getPetIdForBird(int birdId) throws SQLException {
        String sql = "SELECT pet_id FROM birds WHERE id = " + birdId;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getInt("pet_id");
            }
        }

        return -1;
    }

    private Bird mapBird(ResultSet resultSet) throws SQLException {
        Bird bird = new Bird();
        bird.setName(resultSet.getString("name"));
        bird.setAge(resultSet.getInt("age"));
        bird.setBreed(resultSet.getString("breed"));
        bird.setGender(resultSet.getString("gender"));
        bird.setVaccinated(resultSet.getInt("vaccinated") != 0);
        bird.setDescription(resultSet.getString("description"));
        bird.setWingSpan(resultSet.getString("wing_span"));
        bird.setCanFly(resultSet.getInt("can_fly") != 0);
        return bird;
    }
}