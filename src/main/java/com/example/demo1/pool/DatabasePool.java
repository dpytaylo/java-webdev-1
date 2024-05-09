package com.example.demo1.pool;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.*;

public class DatabasePool {
    private static final Logger logger = LogManager.getLogger(DatabasePool.class);
    private static final String DRIVER_CLASS_NAME = "org.postgresql.Driver";
    public static final String URL = "jdbc:postgresql://localhost:5432/webdev";
    public static final String POSTGRES_USERNAME = "postgres";
    public static final String POSTGRES_PASSWORD = "password";

    private static final String CREATE_USERS_TABLE_IF_NOT_EXISTS = """
        CREATE TABLE IF NOT EXISTS public.users (
            id BIGSERIAL PRIMARY KEY,
            email TEXT UNIQUE NOT NULL,
            password TEXT NOT NULL,
            name VARCHAR(30) UNIQUE NOT NULL,
            age INT NOT NULL,
            is_confirmed BOOLEAN NOT NULL,
            avatar BYTEA
        );
""";

    private static final String CREATE_SESSIONS_TABLE_IF_NOT_EXISTS = """
        CREATE TABLE IF NOT EXISTS public.sessions (
            session_id TEXT PRIMARY KEY,
            user_id BIGINT NOT NULL REFERENCES public.users(id)
        );
    """;

    private static final String CREATE_CONFIRMATION_TOKENS_TABLE_IF_NOT_EXISTS = """
        CREATE TABLE IF NOT EXISTS public.confirmation_tokens (
            user_id BIGINT PRIMARY KEY REFERENCES public.users(id),
            token TEXT
        );
    """;

    private static DataSource dataSource;

    public static void initializeDataSource() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(DRIVER_CLASS_NAME);
        basicDataSource.setUrl(URL);
        basicDataSource.setUsername(POSTGRES_USERNAME);
        basicDataSource.setPassword(POSTGRES_PASSWORD);

        basicDataSource.setInitialSize(5);
        basicDataSource.setMaxTotal(10);

        dataSource = basicDataSource;

        try (final var connection = getConnection()) {
            try (Statement statement = connection.createStatement()){
                statement.execute(CREATE_USERS_TABLE_IF_NOT_EXISTS);
                statement.execute(CREATE_SESSIONS_TABLE_IF_NOT_EXISTS);
                statement.execute(CREATE_CONFIRMATION_TOKENS_TABLE_IF_NOT_EXISTS);
            }
        } catch(SQLException e){
            logger.fatal("Failed to initialize database", e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}