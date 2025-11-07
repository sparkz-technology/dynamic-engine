package com.io.sdk.dynamic_engine.auth.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.io.sdk.dynamic_engine.auth.entity.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long jwtRefreshExpirationMs;

    private Algorithm algorithm() {
        return Algorithm.HMAC256(jwtSecret.getBytes());
    }

    public String generateToken(String email, Set<Role> roles) {
        String rolesCsv = roles.stream()
                .map(Role::getName)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationMs);

        return JWT.create()
                .withSubject(email)
                .withClaim("roles", rolesCsv)
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(algorithm());
    }

    public String generateRefreshToken(String email) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtRefreshExpirationMs);

        return JWT.create()
                .withSubject(email)
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(algorithm());
    }

    public boolean validateToken(String token) {
        try {
            JWT.require(algorithm()).build().verify(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        DecodedJWT jwt = JWT.require(algorithm()).build().verify(token);
        return jwt.getSubject();
    }

    public String getEmailFromToken(String token) {
        DecodedJWT jwt = JWT.require(algorithm()).build().verify(token);
        return jwt.getSubject();
    }

    public Set<String> getRolesFromToken(String token) {
        DecodedJWT jwt = JWT.require(algorithm()).build().verify(token);
        String csv = jwt.getClaim("roles").asString();
        if (csv == null || csv.isBlank())
            return Set.of();
        return Set.of(csv.split(","));
    }
}
