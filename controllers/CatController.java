package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseManager.DatabaseManager;
import models.petModels.Cat.Cat;

public class CatController {
    private final Connection connection;
    private final PetController petController;

    public CatController() {
        this.connection = DatabaseManager.getInstance().getConnection();
        this.petController = new PetController();
    }

    public int createCat(Cat cat) {
        try {
            connection.setAutoCommit(false);

            int petId = petController.createPet(cat, "Cat");
            String sql = "INSERT INTO cats (pet_id, fur_color, litter_trained) VALUES ("
                    + petId + ", '" + cat.getFurColor() + "', "
                    + (cat.isLitterTrained() ? 1 : 0) + ")";

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
                throw new RuntimeException("Failed to rollback cat creation: " + rollbackException.getMessage(), rollbackException);
            }
            throw new RuntimeException("Failed to create cat: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to restore auto-commit mode: " + e.getMessage(), e);
            }
        }

        return -1;
    }

    public Cat getCatById(int catId) {
        String sql = "SELECT c.id AS cat_id, p.name, p.age, p.breed, p.gender, p.vaccinated, p.description, c.fur_color, c.litter_trained " +
                     "FROM cats c JOIN pets p ON c.pet_id = p.id WHERE c.id = " + catId;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return mapCat(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch cat: " + e.getMessage(), e);
        }

        return null;
    }

    public List<Cat> getAllCats() {
        List<Cat> cats = new ArrayList<>();
        String sql = "SELECT c.id AS cat_id, p.name, p.age, p.breed, p.gender, p.vaccinated, p.description, c.fur_color, c.litter_trained " +
                     "FROM cats c JOIN pets p ON c.pet_id = p.id ORDER BY c.id";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                cats.add(mapCat(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch cats: " + e.getMessage(), e);
        }

        return cats;
    }

    public boolean updateCat(int catId, Cat cat) {
        try {
            connection.setAutoCommit(false);

            int petId = getPetIdForCat(catId);
            if (petId == -1) {
                connection.rollback();
                return false;
            }

            boolean petUpdated = petController.updatePet(petId, cat, "Cat");

            String sql = "UPDATE cats SET fur_color = '" + cat.getFurColor() + "', litter_trained = "
                    + (cat.isLitterTrained() ? 1 : 0) + " WHERE id = " + catId;
            try (Statement statement = connection.createStatement()) {

                boolean catUpdated = statement.executeUpdate(sql) > 0;
                connection.commit();
                return petUpdated && catUpdated;
            }
        } catch (SQLException | RuntimeException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Failed to rollback cat update: " + rollbackException.getMessage(), rollbackException);
            }
            throw new RuntimeException("Failed to update cat: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to restore auto-commit mode: " + e.getMessage(), e);
            }
        }
    }

    public boolean deleteCat(int catId) {
        try {
            connection.setAutoCommit(false);

            int petId = getPetIdForCat(catId);
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
                throw new RuntimeException("Failed to rollback cat deletion: " + rollbackException.getMessage(), rollbackException);
            }
            throw new RuntimeException("Failed to delete cat: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to restore auto-commit mode: " + e.getMessage(), e);
            }
        }
    }

    private int getPetIdForCat(int catId) throws SQLException {
        String sql = "SELECT pet_id FROM cats WHERE id = " + catId;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getInt("pet_id");
            }
        }

        return -1;
    }

    private Cat mapCat(ResultSet resultSet) throws SQLException {
        Cat cat = new Cat();
        cat.setName(resultSet.getString("name"));
        cat.setAge(resultSet.getInt("age"));
        cat.setBreed(resultSet.getString("breed"));
        cat.setGender(resultSet.getString("gender"));
        cat.setVaccinated(resultSet.getInt("vaccinated") != 0);
        cat.setDescription(resultSet.getString("description"));
        cat.setFurColor(resultSet.getString("fur_color"));
        cat.setLitterTrained(resultSet.getInt("litter_trained") != 0);
        return cat;
    }
}