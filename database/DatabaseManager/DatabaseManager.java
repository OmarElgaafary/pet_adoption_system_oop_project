package database.DatabaseManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static DatabaseManager instance;

    private Connection conn;
    private static final String DB_URL = "jdbc:sqlite:petopia.db";

    private DatabaseManager() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
            initializeDatabase();
            System.out.println("Connected to database successfully.");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

    private void initializeDatabase() {
        createUsersTable();
        createAdoptersTable();
        createPetsTable();
        createDogsTable();
        createCatsTable();
        createBirdsTable();
        createAdoptionsTable();
    }

    private void createUsersTable() {
        try (Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "first_name TEXT NOT NULL," +
                         "last_name TEXT NOT NULL," +
                         "age INTEGER NOT NULL," +
                         "email_address TEXT NOT NULL UNIQUE," +
                         "password TEXT NOT NULL" +
                         ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creating users table: " + e.getMessage());
        }
    }

    private void createAdoptersTable() {
        try (Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS adopters (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "user_id INTEGER NOT NULL UNIQUE," +
                         "phone_number INTEGER NOT NULL," +
                         "address TEXT NOT NULL," +
                         "fav_pet_type TEXT," +
                         "account_balance REAL NOT NULL DEFAULT 0.0," +
                         "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE" +
                         ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creating adopters table: " + e.getMessage());
        }
    }

    private void createPetsTable() {
        try (Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS pets (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "name TEXT NOT NULL," +
                         "age INTEGER NOT NULL," +
                         "breed TEXT NOT NULL," +
                         "gender TEXT NOT NULL," +
                         "vaccinated INTEGER NOT NULL," +
                         "description TEXT," +
                         "type TEXT NOT NULL," +
                         "adopted INTEGER NOT NULL DEFAULT 0" +
                         ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creating pets table: " + e.getMessage());
        }
    }

    private void createDogsTable() {
        try (Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS dogs (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "pet_id INTEGER NOT NULL UNIQUE," +
                         "size TEXT NOT NULL," +
                         "trained INTEGER NOT NULL," +
                         "FOREIGN KEY(pet_id) REFERENCES pets(id) ON DELETE CASCADE" +
                         ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creating dogs table: " + e.getMessage());
        }
    }

    private void createCatsTable() {
        try (Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS cats (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "pet_id INTEGER NOT NULL UNIQUE," +
                         "fur_color TEXT NOT NULL," +
                         "litter_trained INTEGER NOT NULL," +
                         "FOREIGN KEY(pet_id) REFERENCES pets(id) ON DELETE CASCADE" +
                         ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creating cats table: " + e.getMessage());
        }
    }

    private void createBirdsTable() {
        try (Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS birds (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "pet_id INTEGER NOT NULL UNIQUE," +
                         "wing_span TEXT NOT NULL," +
                         "can_fly INTEGER NOT NULL," +
                         "FOREIGN KEY(pet_id) REFERENCES pets(id) ON DELETE CASCADE" +
                         ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creating birds table: " + e.getMessage());
        }
    }

    private void createAdoptionsTable() {
        try (Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS adoptions (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "adopter_id INTEGER NOT NULL," +
                         "pet_id INTEGER NOT NULL UNIQUE," +
                         "adopted_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                         "FOREIGN KEY(adopter_id) REFERENCES adopters(id) ON DELETE CASCADE," +
                         "FOREIGN KEY(pet_id) REFERENCES pets(id) ON DELETE CASCADE" +
                         ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creating adoptions table: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    // Singleton Pattern
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
}
