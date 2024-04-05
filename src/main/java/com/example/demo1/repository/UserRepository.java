package com.example.demo1.repository;

import com.example.demo1.entity.User;
import com.example.demo1.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;

public class UserRepository {
    private static final String GET_ALL_QUERY = "SELECT id, email, password, name, age FROM users ORDER BY id";
    private static final String FIND_BY_USER_ID_QUERY = "SELECT id, email, password, name, age FROM users WHERE id = ?";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT id, email, password, name, age FROM users WHERE email = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT id, email, password, name, age FROM users WHERE name = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (email, password, name, age) VALUES (?, ?, ?, ?)";
    private static final String MODIFY_QUERY = "UPDATE users SET name = ?, age = ? WHERE id = ?";

    public ArrayList<User> getAll() throws SQLException {
        ArrayList<User> users = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(
            DatabaseConfig.JDBC_URL,
            DatabaseConfig.POSTGRES_USERNAME,
            DatabaseConfig.POSTGRES_PASSWORD
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

    public User findByUserId(long userId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(
            DatabaseConfig.JDBC_URL,
            DatabaseConfig.POSTGRES_USERNAME,
            DatabaseConfig.POSTGRES_PASSWORD
        )) {
            try (PreparedStatement statement = connection.prepareStatement(FIND_BY_USER_ID_QUERY)) {
                statement.setLong(1, userId);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return getUserFromResultSet(resultSet);
                }
            }
        }

        return null;
    }

    public User findByEmail(String email) throws SQLException {
        try (Connection connection = DriverManager.getConnection(
            DatabaseConfig.JDBC_URL,
            DatabaseConfig.POSTGRES_USERNAME,
            DatabaseConfig.POSTGRES_PASSWORD
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

    public User findByName(String name) throws SQLException {
        try (Connection connection = DriverManager.getConnection(
            DatabaseConfig.JDBC_URL,
            DatabaseConfig.POSTGRES_USERNAME,
            DatabaseConfig.POSTGRES_PASSWORD
        )) {
            try (PreparedStatement statement = connection.prepareStatement(FIND_BY_NAME_QUERY)) {
                statement.setString(1, name);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return getUserFromResultSet(resultSet);
                }
            }
        }

        return null;
    }
    
    private User getUserFromResultSet(ResultSet set) throws SQLException {
        final var id = set.getLong("id");
        final var email = set.getString("email");
        final var password = set.getString("password");
        final var name = set.getString("name");
        final var age = set.getInt("age");

        return new User(id, email, password, name, age);
    }

    public User insert(
        String email,
        String password,
        String name,
        int age
    ) throws SQLException {
        long id;

        try (Connection connection = DriverManager.getConnection(
            DatabaseConfig.JDBC_URL,
            DatabaseConfig.POSTGRES_USERNAME,
            DatabaseConfig.POSTGRES_PASSWORD
        )) {
            try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, email);
                statement.setString(2, password);
                statement.setString(3, name);
                statement.setInt(4, age);
                
                statement.executeUpdate();

                final var resultSet = statement.getGeneratedKeys();
                resultSet.next();

                id = resultSet.getLong(1);
            }
        }

        return new User(id, email, password, name, age);
    }

    public void modify(long userId, String name, int age) throws SQLException {
        try (Connection connection = DriverManager.getConnection(
            DatabaseConfig.JDBC_URL,
            DatabaseConfig.POSTGRES_USERNAME,
            DatabaseConfig.POSTGRES_PASSWORD
        )) {
            try (PreparedStatement statement = connection.prepareStatement(MODIFY_QUERY)) {
                statement.setString(1, name);
                statement.setInt(2, age);
                statement.setLong(3, userId);

                statement.executeUpdate();
            }
        }
    }
}
