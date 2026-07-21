package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class managing a single shared database connection.
 * @author D00276269
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/gospel_catalogue?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    // Private constructor prevents direct instantiation
    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}