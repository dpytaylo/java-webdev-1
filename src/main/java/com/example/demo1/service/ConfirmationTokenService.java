package com.example.demo1.service;

import com.example.demo1.repository.UserRepository;
import com.example.demo1.repository.ConfirmationTokenRepository;

import java.sql.SQLException;
import java.util.Random;

public class ConfirmationTokenService {
    private final ConfirmationTokenRepository repository;
    private final UserRepository userRepository;

    public ConfirmationTokenService(ConfirmationTokenRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public String generateToken(long userId) throws SQLException {
        Random rand = new Random();
        int randomNumber = rand.nextInt(10000);
        String token = String.format("%04d", randomNumber);

        repository.insert(token, userId);
        return token;
    }

    public boolean checkConfirmationToken(long userId, String token) throws SQLException {
        final var dbToken = repository.findByUserId(userId);

        if (dbToken.map(s -> s.equals(token)).orElse(false)) {
            userRepository.setConfirmedEmail(userId);
            return true;
        } else {
            return false;
        }
    }
}
