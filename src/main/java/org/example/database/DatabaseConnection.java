package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private static final String URL =
            "jdbc:mysql://localhost:3306/auto_parts_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private DatabaseConnection() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static synchronized DatabaseConnection getInstance()
            throws SQLException {

        if (instance == null) {
            instance = new DatabaseConnection();
        }

        return instance;
    }

    public Connection getConnection() throws SQLException {

        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }

        return connection;
    }
}