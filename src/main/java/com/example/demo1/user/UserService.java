package com.example.demo1.user;

import com.example.demo1.exception.BadFormInputException;
import com.password4j.Password;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class UserService {
    private static final int PASSWORD_SALT_LENGTH = 32;

    private static final Logger logger = LogManager.getLogger(UserService.class);
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User findById(long userId) throws SQLException {
        return repository.findByUserId(userId);
    }

    public ArrayList<User> getAll() throws SQLException {
        return repository.getAll();
    }

    public Optional<User> signIn(String email, String password) throws SQLException {
        User user = repository.findByEmail(email);
        if (user == null) {
            return Optional.empty();
        }

        if (!Password.check(password, user.getPassword()).withArgon2()) {
            return Optional.empty();
        }

        return Optional.of(user);
    }

    public User signUp(
        String email,
        String password,
        String name,
        int age
    ) throws BadFormInputException, SQLException {
        if (repository.findByEmail(email) != null) {
            throw new BadFormInputException("User with this email already exists");
        }

        final var passwordByteLength = password.getBytes().length;
        if (passwordByteLength < 8 || passwordByteLength > 32) {
            throw new BadFormInputException("Password should be in 8 to 32 bytes length");
        }

        final var hashedPassword = Password.hash(password).addRandomSalt(PASSWORD_SALT_LENGTH).withArgon2();

        if (!name.matches("^\\p{ASCII}*$") || name.isEmpty() || name.length() > 32) {
            throw new BadFormInputException("Name should contain only ASCII characters and be no longer than 32 characters");
        }

        if (repository.findByName(name) != null) {
            throw new BadFormInputException("User with this name already exists");
        }

        if (age < 0) {
            throw new BadFormInputException("Age should be a positive number");
        }

        return repository.insert(email, hashedPassword.getResult(), name, age);
    }
}
