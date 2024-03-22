package com.example.demo1.user;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record UserDto(String email, String password, String name, Integer age) {
}
