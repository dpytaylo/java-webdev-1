package com.example.demo1.repository;

import com.example.demo1.pool.DatabasePool;

import java.sql.SQLException;
import java.util.Optional;

public class ConfirmationTokenRepository {
    private static final String INSERT_QUERY = "INSERT INTO confirmation_tokens (user_id, token) VALUES (?, ?)";
    private static final String FIND_BY_USER_ID_QUERY = "SELECT token FROM confirmation_tokens WHERE user_id = ?";

    public void insert(String token, long userId) throws SQLException {
        try (final var connection = DatabasePool.getConnection()) {
            try (final var statement = connection.prepareStatement(INSERT_QUERY)) {
                statement.setLong(1, userId);
                statement.setString(2, token);

                statement.executeUpdate();
            }
        }
    }

    public Optional<String> findByUserId(long userId) throws SQLException {
        try (final var connection = DatabasePool.getConnection()) {
            try (final var statement = connection.prepareStatement(FIND_BY_USER_ID_QUERY)) {
                statement.setLong(1, userId);

                final var resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    final var token = resultSet.getString("token");
                    return Optional.of(token);
                }
            }
        }

        return Optional.empty();
    }
}
