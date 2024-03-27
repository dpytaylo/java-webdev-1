package com.example.demo1.session;

import com.example.demo1.util.DatabaseConfig;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class SessionRepository {
    private static final String FIND_BY_SESSION_ID_QUERY = "SELECT user_id FROM sessions WHERE session_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO sessions (session_id, user_id) VALUES (?, ?)";

    public Optional<Long> findBySessionId(String sessionId) throws SQLException {
        try (final var connection = DriverManager.getConnection(
            DatabaseConfig.JDBC_URL,
            DatabaseConfig.POSTGRES_USERNAME,
            DatabaseConfig.POSTGRES_PASSWORD
        )) {
            try (final var statement = connection.prepareStatement(FIND_BY_SESSION_ID_QUERY)) {
                statement.setString(1, sessionId);

                final var resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    final var user_id = resultSet.getLong("user_id");
                    return Optional.of(user_id);
                }
            }
        }

        return Optional.empty();
    }

    public void insert(String sessionId, long userId) throws SQLException {
        try (final var connection = DriverManager.getConnection(
            DatabaseConfig.JDBC_URL,
            DatabaseConfig.POSTGRES_USERNAME,
            DatabaseConfig.POSTGRES_PASSWORD
        )) {
            try (final var statement = connection.prepareStatement(INSERT_QUERY)) {
                statement.setString(1, sessionId);
                statement.setLong(2, userId);

                statement.executeUpdate();
            }
        }
    }
}
