package com.example.demo1.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;

public class JwtTokens {
    // TODO move it in a configuration file
    private final static SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();

    public static String generateJwtToken(long userId) {
        return Jwts.builder()
            .subject(String.valueOf(userId))
            .signWith(SECRET_KEY)
            .compact();
    }

    public static String generateBearerToken(long userId) {
        return "Bearer " + generateJwtToken(userId);
    }

    public static long parseBearerToken(String jwt) {
        String token = jwt.split(" ")[1];
        Jws<Claims> jws = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token);
        return Long.parseLong(jws.getPayload().getSubject());
    }
}
