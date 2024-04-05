package com.example.demo1.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class Users {
    ArrayList<User> values;
}
