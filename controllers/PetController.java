package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseManager.DatabaseManager;
import models.petModels.Pet.Pet;

public class PetController {
    private final Connection connection;

    public PetController() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    public int createPet(Pet pet, String type) {
        String sql = "INSERT INTO pets (name, age, breed, gender, vaccinated, description, type, adopted) VALUES ('"
            + pet.getName() + "', "
            + pet.getAge() + ", '"
            + pet.getBreed() + "', '"
            + pet.getGender() + "', "
            + (Boolean.TRUE.equals(pet.getVaccinated()) ? 1 : 0) + ", '"
            + pet.getDescription() + "', '"
            + type + "', "
            + pet.getAdopted() + ")";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create pet: " + e.getMessage(), e);
        }

        return -1;
    }

    public Pet getPetById(int petId) {
        String sql = "SELECT * FROM pets WHERE id = " + petId;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return mapPet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch pet: " + e.getMessage(), e);
        }

        return null;
    }

    public List<Pet> getAllPets() {
        List<Pet> pets = new ArrayList<>();
        String sql = "SELECT * FROM pets WHERE adopted = 0 ORDER BY id";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                pets.add(mapPet(resultSet));
            }

            System.out.println("Fetched " + pets.size() + " pets from the database.");
        } catch (SQLException e) {
            System.out.println("Error fetching pets: " + e.getMessage());
            throw new RuntimeException("Failed to fetch pets: " + e.getMessage(), e);
        }

        return pets;
    }

    public boolean updatePet(int petId, Pet pet, String type) {
        String sql = "UPDATE pets SET name = '" + pet.getName() + "', age = " + pet.getAge() + ", breed = '"
                + pet.getBreed() + "', gender = '" + pet.getGender() + "', vaccinated = "
                + (Boolean.TRUE.equals(pet.getVaccinated()) ? 1 : 0) + ", description = '"
            + pet.getDescription() + "', type = '" + type + "', adopted = " + pet.getAdopted() + " WHERE id = " + petId;

        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update pet: " + e.getMessage(), e);
        }
    }

    public boolean deletePet(int petId) {
        String sql = "DELETE FROM pets WHERE id = " + petId;

        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete pet: " + e.getMessage(), e);
        }
    }

    private Pet mapPet(ResultSet resultSet) throws SQLException {
        Pet pet = new Pet();
        pet.setPetId(resultSet.getInt("id"));
        pet.setName(resultSet.getString("name"));
        pet.setAge(resultSet.getInt("age"));
        pet.setBreed(resultSet.getString("breed"));
        pet.setGender(resultSet.getString("gender"));
        pet.setVaccinated(resultSet.getInt("vaccinated") != 0);
        pet.setDescription(resultSet.getString("description"));
        pet.setAdopted(resultSet.getInt("adopted"));
        return pet;
    }
}