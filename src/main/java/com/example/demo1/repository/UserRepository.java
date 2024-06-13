package com.example.demo1.repository;

import com.example.demo1.entity.User;
import com.example.demo1.pool.DatabasePool;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class UserRepository {
    private static final String GET_ALL_QUERY = "SELECT id, email, password, name, age, is_confirmed FROM users ORDER BY id";
    private static final String FIND_BY_USER_ID_QUERY = "SELECT id, email, password, name, age, is_confirmed FROM users WHERE id = ?";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT id, email, password, name, age, is_confirmed FROM users WHERE email = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT id, email, password, name, age, is_confirmed FROM users WHERE name = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (email, password, name, age, is_confirmed) VALUES (?, ?, ?, ?, ?)";
    private static final String MODIFY_QUERY = "UPDATE users SET name = ?, age = ? WHERE id = ?";
    private static final String SET_CONFIRMED_EMAIL_QUERY = "UPDATE users SET is_confirmed = TRUE WHERE id = ?";
    private static final String GET_AVATAR_BY_USER_ID = "SELECT avatar FROM users WHERE id = ?";
    private static final String MODIFY_AVATAR_BY_USER_ID = "UPDATE users SET avatar = ? WHERE id = ?";

    public ArrayList<User> getAll() throws SQLException {
        ArrayList<User> users = new ArrayList<>();

        try (final var connection = DatabasePool.getConnection()) {
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
        try (final var connection = DatabasePool.getConnection()) {
            try (final var statement = connection.prepareStatement(FIND_BY_USER_ID_QUERY)) {
                statement.setLong(1, userId);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return getUserFromResultSet(resultSet);
                }
            }
        }

        return null;
    }

    private User findUser(String email, String findByEmailQuery) throws SQLException {
        try (final var connection = DatabasePool.getConnection()) {
            try (final var statement = connection.prepareStatement(findByEmailQuery)) {
                statement.setString(1, email);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return getUserFromResultSet(resultSet);
                }
            }
        }

        return null;
    }

    public User findByEmail(String email) throws SQLException {
        return findUser(email, FIND_BY_EMAIL_QUERY);
    }

    public User findByName(String name) throws SQLException {
        return findUser(name, FIND_BY_NAME_QUERY);
    }
    
    private User getUserFromResultSet(ResultSet set) throws SQLException {
        final var id = set.getLong("id");
        final var email = set.getString("email");
        final var password = set.getString("password");
        final var name = set.getString("name");
        final var age = set.getInt("age");
        final var isConfirmed = set.getBoolean("is_confirmed");

        return new User(id, email, password, name, age, isConfirmed);
    }

    public User insert(
        String email,
        String password,
        String name,
        int age,
        boolean isConfirmed
    ) throws SQLException {
        long id;

        try (final var connection = DatabasePool.getConnection()) {
            try (final var statement = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, email);
                statement.setString(2, password);
                statement.setString(3, name);
                statement.setInt(4, age);
                statement.setBoolean(5, isConfirmed);
                
                statement.executeUpdate();

                final var resultSet = statement.getGeneratedKeys();
                resultSet.next();

                id = resultSet.getLong(1);
            }
        }

        return new User(id, email, password, name, age, isConfirmed);
    }

    public void modify(long userId, String name, int age) throws SQLException {
        try (final var connection = DatabasePool.getConnection()) {
            try (final var statement = connection.prepareStatement(MODIFY_QUERY)) {
                statement.setString(1, name);
                statement.setInt(2, age);
                statement.setLong(3, userId);

                statement.executeUpdate();
            }
        }
    }

    public void setConfirmedEmail(long userId) throws SQLException {
        try (final var connection = DatabasePool.getConnection()) {
            try (final var statement = connection.prepareStatement(SET_CONFIRMED_EMAIL_QUERY)) {
                statement.setLong(1, userId);
                statement.executeUpdate();
            }
        }
    }

    public Optional<byte[]> getAvatarByUserId(long userId) throws SQLException {
        try (final var connection = DatabasePool.getConnection()) {
            try (final var statement = connection.prepareStatement(GET_AVATAR_BY_USER_ID, Statement.RETURN_GENERATED_KEYS)) {
                statement.setLong(1, userId);

                final var resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    final var avatar = resultSet.getBytes(1);
                    return Optional.ofNullable(avatar);
                }
            }
        }

        throw new RuntimeException("Invalid user id");
    }

    public void modifyAvatar(long userId, byte[] data) throws SQLException {
        try (final var connection = DatabasePool.getConnection()) {
            try (final var statement = connection.prepareStatement(MODIFY_AVATAR_BY_USER_ID)) {
                statement.setBytes(1, data);
                statement.setLong(2, userId);
                statement.executeUpdate();
            }
        }
    }
}
