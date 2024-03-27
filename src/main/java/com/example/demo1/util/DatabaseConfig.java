package com.example.demo1.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    private static DatabaseConfig instance;

    public static final String JDBC_URL = "jdbc:postgresql://localhost:5432/webdev";
    public static final String POSTGRES_USERNAME = "postgres";
    public static final String POSTGRES_PASSWORD = "password";

    private static final String CREATE_USERS_TABLE_IF_NOT_EXISTS
        = "CREATE TABLE IF NOT EXISTS public.users (" +
        "id BIGSERIAL PRIMARY KEY," +
        "email TEXT UNIQUE NOT NULL," +
        "password TEXT NOT NULL," +
        "name VARCHAR(30) UNIQUE NOT NULL," +
        "age INT NOT NULL" +
        ")";

    private static final String CREATE_SESSIONS_TABLE_IF_NOT_EXISTS
        = "CREATE TABLE IF NOT EXISTS public.sessions (" +
        "session_id TEXT UNIQUE NOT NULL," +
        "user_id BIGINT NOT NULL REFERENCES public.users(id)" +
        ")";

    public DatabaseConfig() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = DriverManager.getConnection(
            JDBC_URL,
            POSTGRES_USERNAME,
            POSTGRES_PASSWORD
        )) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(CREATE_USERS_TABLE_IF_NOT_EXISTS);
                statement.execute(CREATE_SESSIONS_TABLE_IF_NOT_EXISTS);
            }
        }
    }

    public DatabaseConfig getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConfig();
        }

        return instance;
    }
}
