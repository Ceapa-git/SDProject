package com.dan.sd.user_management.security;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtGenerator {
    private final Keys keys;

    public JwtGenerator(Keys keys) {
        this.keys = keys;
    }

    public String generateToken(String username, Long ttl) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ttl))
                .signWith(keys.getPrivateKey())
                .compact();
    }
}
