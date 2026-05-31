package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseManager.DatabaseManager;
import models.petModels.Dog.Dog;

public class DogController {
    private final Connection connection;
    private final PetController petController;

    public DogController() {
        this.connection = DatabaseManager.getInstance().getConnection();
        this.petController = new PetController();
    }

    public int createDog(Dog dog) {
        try {
            connection.setAutoCommit(false);

            int petId = petController.createPet(dog, "Dog");
            String sql = "INSERT INTO dogs (pet_id, size, trained) VALUES ("
                    + petId + ", '" + dog.getSize() + "', "
                    + (dog.isTrained() ? 1 : 0) + ")";

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
                throw new RuntimeException("Failed to rollback dog creation: " + rollbackException.getMessage(), rollbackException);
            }
            throw new RuntimeException("Failed to create dog: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to restore auto-commit mode: " + e.getMessage(), e);
            }
        }

        return -1;
    }

    public Dog getDogById(int dogId) {
        String sql = "SELECT d.id AS dog_id, p.name, p.age, p.breed, p.gender, p.vaccinated, p.description, d.size, d.trained " +
                     "FROM dogs d JOIN pets p ON d.pet_id = p.id WHERE d.id = " + dogId;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return mapDog(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch dog: " + e.getMessage(), e);
        }

        return null;
    }

    public List<Dog> getAllDogs() {
        List<Dog> dogs = new ArrayList<>();
        String sql = "SELECT d.id AS dog_id, p.name, p.age, p.breed, p.gender, p.vaccinated, p.description, d.size, d.trained " +
                     "FROM dogs d JOIN pets p ON d.pet_id = p.id ORDER BY d.id";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                dogs.add(mapDog(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch dogs: " + e.getMessage(), e);
        }

        return dogs;
    }

    public boolean updateDog(int dogId, Dog dog) {
        try {
            connection.setAutoCommit(false);

            int petId = getPetIdForDog(dogId);
            if (petId == -1) {
                connection.rollback();
                return false;
            }

            boolean petUpdated = petController.updatePet(petId, dog, "Dog");

            String sql = "UPDATE dogs SET size = '" + dog.getSize() + "', trained = "
                    + (dog.isTrained() ? 1 : 0) + " WHERE id = " + dogId;
            try (Statement statement = connection.createStatement()) {

                boolean dogUpdated = statement.executeUpdate(sql) > 0;
                connection.commit();
                return petUpdated && dogUpdated;
            }
        } catch (SQLException | RuntimeException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Failed to rollback dog update: " + rollbackException.getMessage(), rollbackException);
            }
            throw new RuntimeException("Failed to update dog: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to restore auto-commit mode: " + e.getMessage(), e);
            }
        }
    }

    public boolean deleteDog(int dogId) {
        try {
            connection.setAutoCommit(false);

            int petId = getPetIdForDog(dogId);
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
                throw new RuntimeException("Failed to rollback dog deletion: " + rollbackException.getMessage(), rollbackException);
            }
            throw new RuntimeException("Failed to delete dog: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to restore auto-commit mode: " + e.getMessage(), e);
            }
        }
    }

    private int getPetIdForDog(int dogId) throws SQLException {
        String sql = "SELECT pet_id FROM dogs WHERE id = " + dogId;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getInt("pet_id");
            }
        }

        return -1;
    }

    private Dog mapDog(ResultSet resultSet) throws SQLException {
        Dog dog = new Dog();
        dog.setName(resultSet.getString("name"));
        dog.setAge(resultSet.getInt("age"));
        dog.setBreed(resultSet.getString("breed"));
        dog.setGender(resultSet.getString("gender"));
        dog.setVaccinated(resultSet.getInt("vaccinated") != 0);
        dog.setDescription(resultSet.getString("description"));
        dog.setSize(resultSet.getString("size"));
        dog.setTrained(resultSet.getInt("trained") != 0);
        return dog;
    }
}