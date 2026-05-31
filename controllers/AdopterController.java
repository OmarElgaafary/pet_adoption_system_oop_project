package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseManager.DatabaseManager;
import models.userModels.Adopter.Adopter;

public class AdopterController {
    private final Connection connection;
    private final UserController userController;

    public AdopterController() {
        this.connection = DatabaseManager.getInstance().getConnection();
        this.userController = new UserController();
    }

    public int createAdopter(Adopter adopter) {
        try {
            connection.setAutoCommit(false);

            int userId = userController.createUser(adopter);
            String sql = "INSERT INTO adopters (user_id, phone_number, address, fav_pet_type, account_balance) VALUES ("
                    + userId + ", "
                    + adopter.getPhoneNumber() + ", '"
                    + adopter.getAddress() + "', '"
                    + adopter.getFavPetType() + "', "
                    + adopter.getAccountBalance() + ")";

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
                throw new RuntimeException("Failed to rollback adopter creation: " + rollbackException.getMessage(), rollbackException);
            }
            throw new RuntimeException("Failed to create adopter: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to restore auto-commit mode: " + e.getMessage(), e);
            }
        }

        return -1;
    }

    public Adopter getAdopterById(int adopterId) {
        String sql = "SELECT a.id AS adopter_id, u.first_name, u.last_name, u.age, u.email_address, u.password, a.phone_number, a.address, a.fav_pet_type, a.account_balance " +
                     "FROM adopters a JOIN users u ON a.user_id = u.id WHERE a.id = " + adopterId;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return mapAdopter(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch adopter: " + e.getMessage(), e);
        }

        return null;
    }

    public List<Adopter> getAllAdopters() {
        List<Adopter> adopters = new ArrayList<>();
        String sql = "SELECT a.id AS adopter_id, u.first_name, u.last_name, u.age, u.email_address, u.password, a.phone_number, a.address, a.fav_pet_type, a.account_balance " +
                     "FROM adopters a JOIN users u ON a.user_id = u.id ORDER BY a.id";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                adopters.add(mapAdopter(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch adopters: " + e.getMessage(), e);
        }

        return adopters;
    }

    public boolean updateAdopter(int adopterId, Adopter adopter) {
        try {
            connection.setAutoCommit(false);

            int userId = getUserIdForAdopter(adopterId);
            if (userId == -1) {
                connection.rollback();
                return false;
            }

            boolean userUpdated = userController.updateUser(userId, adopter);

            String sql = "UPDATE adopters SET phone_number = " + adopter.getPhoneNumber() + ", address = '"
                    + adopter.getAddress() + "', fav_pet_type = '" + adopter.getFavPetType() + "', account_balance = "
                    + adopter.getAccountBalance() + " WHERE id = " + adopterId;
            try (Statement statement = connection.createStatement()) {
                boolean adopterUpdated = statement.executeUpdate(sql) > 0;
                connection.commit();
                return userUpdated && adopterUpdated;
            }
        } catch (SQLException | RuntimeException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Failed to rollback adopter update: " + rollbackException.getMessage(), rollbackException);
            }
            throw new RuntimeException("Failed to update adopter: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to restore auto-commit mode: " + e.getMessage(), e);
            }
        }
    }

    public boolean deleteAdopter(int adopterId) {
        try {
            connection.setAutoCommit(false);

            int userId = getUserIdForAdopter(adopterId);
            if (userId == -1) {
                connection.rollback();
                return false;
            }

            boolean deleted = userController.deleteUser(userId);
            connection.commit();
            return deleted;
        } catch (SQLException | RuntimeException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Failed to rollback adopter deletion: " + rollbackException.getMessage(), rollbackException);
            }
            throw new RuntimeException("Failed to delete adopter: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to restore auto-commit mode: " + e.getMessage(), e);
            }
        }
    }

    private int getUserIdForAdopter(int adopterId) throws SQLException {
        String sql = "SELECT user_id FROM adopters WHERE id = " + adopterId;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getInt("user_id");
            }
        }

        return -1;
    }

    private Adopter mapAdopter(ResultSet resultSet) throws SQLException {
        Adopter adopter = new Adopter();
        adopter.setFirstName(resultSet.getString("first_name"));
        adopter.setLastName(resultSet.getString("last_name"));
        adopter.setAge(resultSet.getInt("age"));
        adopter.setEmailAddress(resultSet.getString("email_address"));
        adopter.setPassword(resultSet.getString("password"));
        adopter.setPhoneNumber(resultSet.getInt("phone_number"));
        adopter.setAddress(resultSet.getString("address"));
        adopter.setFavPetType(resultSet.getString("fav_pet_type"));
        adopter.setAccountBalance(resultSet.getDouble("account_balance"));
        return adopter;
    }
}