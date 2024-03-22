package com.example.demo1.user;

import java.sql.*;
import java.util.ArrayList;

public class UserRepository {
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/webdev";
    public static final String POSTGRES_USERNAME = "postgres";
    public static final String POSTGRES_PASSWORD = "password";
    private static final String CREATE_USERS_TABLE_IF_NOT_EXISTS
        = "CREATE TABLE IF NOT EXISTS users (" +
            "id BIGSERIAL," +
            "email TEXT UNIQUE NOT NULL," +
            "password TEXT NOT NULL," +
            "password_salt TEXT NOT NULL," +
            "name VARCHAR(30) UNIQUE NOT NULL," +
            "age INT NOT NULL" +
        ")";
    private static final String GET_ALL_QUERY = "SELECT id, email, password, password_salt, name, age FROM users";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT id, email, password, password_salt, name, age FROM users WHERE email = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (email, password, password_salt, name, age) VALUES (?, ?, ?, ?, ?)";

    public UserRepository() throws Exception {
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
            }
        }
    }

    public ArrayList<User> getAll() throws SQLException {
        ArrayList<User> users = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(
            JDBC_URL,
            POSTGRES_USERNAME,
            POSTGRES_PASSWORD
        )) {
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(GET_ALL_QUERY);

                while (resultSet.next()) {
                    users.add(getUserFromResultSet(resultSet));
                }
            }
        }

        return users;
    }
    
    public User findByEmail(String email) throws SQLException {
        try (Connection connection = DriverManager.getConnection(
            JDBC_URL,
            POSTGRES_USERNAME,
            POSTGRES_PASSWORD
        )) {
            try (PreparedStatement statement = connection.prepareStatement(FIND_BY_EMAIL_QUERY)) {
                statement.setString(1, email);
                
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return getUserFromResultSet(resultSet);
                }
            }
        }
        
        return null;
    }
    
    private User getUserFromResultSet(ResultSet set) throws SQLException {
        Long id = set.getLong("id");
        String email = set.getString("email");
        String password = set.getString("password");
        String passwordSalt = set.getString("password_salt");
        String name = set.getString("name");
        Integer age = set.getInt("age");

        return new User(id, email, password, passwordSalt, name, age);
    }

    public void insert(UserDto user, String password, String passwordSalt) throws SQLException {
        try (Connection connection = DriverManager.getConnection(
            JDBC_URL,
            POSTGRES_USERNAME,
            POSTGRES_PASSWORD
        )) {
            try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY)) {
                statement.setString(1, user.email());
                statement.setString(2, password);
                statement.setString(3, passwordSalt);
                statement.setString(4, user.name());
                statement.setInt(5, user.age());
                
                statement.executeUpdate();
            }
        }
    }
}
