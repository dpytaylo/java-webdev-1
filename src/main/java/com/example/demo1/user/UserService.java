package com.example.demo1.user;

import java.sql.SQLException;
import java.util.ArrayList;

public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public ArrayList<User> getAll() throws SQLException {
        return repository.getAll();
    }

    public void create(UserDto user) throws SQLException {
        repository.insert(user, user.password(), "");
    }

    public boolean signIn(String email, String password) throws SQLException {
        User user = repository.findByEmail(email);
        if (user == null) {
            return false;
        }

        return user.getPassword().equals(password);
    }
}
