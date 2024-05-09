package com.example.demo1.service;

import com.example.demo1.controller.exception.BadFormInputException;
import com.example.demo1.repository.ConfirmationTokenRepository;
import com.example.demo1.repository.UserRepository;
import com.example.demo1.entity.User;
import com.example.demo1.service.exception.FailedToSendEmail;
import com.password4j.Password;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;

public class UserService {
    public static final String EMAIL_SENDER_EMAIL = "stdlabconf@gmail.com";
    public static final String EMAIL_SENDER_PASSWORD = "qzwwsjdgnvefdunu";
    private static final int PASSWORD_SALT_LENGTH = 32;
    public static final String EMAIL_SENDER_HOST = "smtp.gmail.com";
    public static final String EMAIL_SENDER_PORT = "587";

    private static final Logger logger = LogManager.getLogger(UserService.class);

    private final UserRepository repository;
    private final ConfirmationTokenService confirmationTokenService;

    public UserService(UserRepository repository) {
        this.repository = repository;
        this.confirmationTokenService = new ConfirmationTokenService(
            new ConfirmationTokenRepository(),
            repository
        );
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
    ) throws BadFormInputException, FailedToSendEmail, SQLException {
        if (repository.findByEmail(email) != null) {
            throw new BadFormInputException("User with this email already exists");
        }

        validatePassword(password);
        final var hashedPassword = Password.hash(password).addRandomSalt(PASSWORD_SALT_LENGTH).withArgon2();

        validateName(name);
        validateAge(age);

        final var user = repository.insert(email, hashedPassword.getResult(), name, age, false);
        sendConfirmationEmail(user.getId(), email);
        return user;
    }

    public void modify(long userId, String name, int age) throws BadFormInputException, SQLException {
        validateName(name);
        validateAge(age);

        repository.modify(userId, name, age);
    }

    private void validatePassword(String password) throws BadFormInputException {
        final var passwordByteLength = password.getBytes().length;
        if (passwordByteLength < 8 || passwordByteLength > 32) {
            throw new BadFormInputException("Password should be in 8 to 32 bytes length");
        }
    }

    private void validateName(String name) throws BadFormInputException, SQLException {
        if (!name.matches("^\\p{ASCII}*$") || name.isEmpty() || name.length() > 32) {
            throw new BadFormInputException("Name should contain only ASCII characters and be no longer than 32 characters");
        }

        if (repository.findByName(name) != null) {
            throw new BadFormInputException("User with this name already exists");
        }
    }

    private void validateAge(int age) throws BadFormInputException {
        if (age < 0) {
            throw new BadFormInputException("Age should be a positive number");
        }
    }

    private void sendConfirmationEmail(long userId, String email) throws FailedToSendEmail, SQLException {
        final var properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", EMAIL_SENDER_HOST);
        properties.put("mail.smtp.port", EMAIL_SENDER_PORT);

        final var session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_SENDER_EMAIL, EMAIL_SENDER_PASSWORD);
            }
        });

        try {
            final var message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_SENDER_EMAIL));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

            message.setSubject("Email Confirmation");
            final var token = confirmationTokenService.generateToken(userId);
            message.setText("Your confirmation token is " + token);

            Transport.send(message);
            logger.info("The email with the '" + token + "' token was sent");
        } catch (MessagingException e) {
            logger.fatal("Failed to send email", e);
            throw new FailedToSendEmail();
        }
    }

    public Optional<byte[]> getAvatarByUserId(long userId) throws SQLException {
        return repository.getAvatarByUserId(userId);
    }

    public void modifyAvatar(long userId, byte[] data) throws SQLException {
        repository.modifyAvatar(userId, data);
    }
}
