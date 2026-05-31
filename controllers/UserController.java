package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseManager.DatabaseManager;
import models.userModels.User.User;

public class UserController {
    private final Connection connection;

    public UserController() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    public int createUser(User user) {
        String sql = "INSERT INTO users (first_name, last_name, age, email_address, password) VALUES ('"
                + user.getFirstName() + "', '"
                + user.getLastName() + "', "
                + user.getAge() + ", '"
                + user.getEmailAddress() + "', '"
                + user.getPassword() + "')";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int userId = keys.getInt(1);
                    user.setUserId(userId);
                    return userId;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }

        return -1;
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = " + userId;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return mapUser(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user: " + e.getMessage(), e);
        }

        return null;
    }

    public User getUserByEmail(String emailAddress) {
        String sql = "SELECT * FROM users WHERE email_address = '" + emailAddress + "'";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return mapUser(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user: " + e.getMessage(), e);
        }

        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch users: " + e.getMessage(), e);
        }

        return users;
    }

    public boolean updateUser(int userId, User user) {
        String sql = "UPDATE users SET first_name = '" + user.getFirstName() + "', last_name = '" + user.getLastName() + "', age = "
                + user.getAge() + ", email_address = '" + user.getEmailAddress() + "', password = '" + user.getPassword() + "' WHERE id = " + userId;

        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        }
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = " + userId;

        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUserId(resultSet.getInt("id"));
        user.setFirstName(resultSet.getString("first_name"));
        user.setLastName(resultSet.getString("last_name"));
        user.setAge(resultSet.getInt("age"));
        user.setEmailAddress(resultSet.getString("email_address"));
        user.setPassword(resultSet.getString("password"));
        return user;
    }
}