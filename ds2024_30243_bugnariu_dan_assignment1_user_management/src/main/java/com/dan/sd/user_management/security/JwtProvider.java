package com.dan.sd.user_management.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {
    private final Keys keys;

    public JwtProvider(Keys keys) {
        this.keys = keys;
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        Logger logger = LoggerFactory.getLogger(JwtProvider.class);
        logger.info(token);
        Claims claims = getClaims(token);

        if (claims.getExpiration().before(new Date())) {
            logger.info("Token expired");
            return false;
        }
        return true;
    }

    public void authenticate(String token) {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(token));
    }

    private Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, null);
    }
    
    private Claims getClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(keys.getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
