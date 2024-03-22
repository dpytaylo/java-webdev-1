package com.example.demo1.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private Long id;
    private String email;
    private String password;
    private String passwordSalt;
    private String name;
    private Integer age;
}
