package com.example.restaurApp.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.restaurApp.entity.Empleado;
import com.example.restaurApp.repository.EmpleadoRepository;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret:ClaveSuperSecreta123456789012345678901234567890}")
    private String secret;

    @Value("${jwt.expiration-ms:3600000}")
    private long expirationMs;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username, String rol,String userId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("rol", rol)
                .claim("id", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String extractRol(String token) {
        return (String) Jwts.parserBuilder().setSigningKey(getKey()).build()
                .parseClaimsJws(token).getBody().get("rol");
    }

    public String extractUserId(String token) {
        Object idClaim = Jwts.parserBuilder().setSigningKey(getKey()).build()
                .parseClaimsJws(token).getBody().get("id");
        return idClaim != null ? idClaim.toString() : null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }

    public Empleado getEmpleadoFromToken(String token) {
        try {
            if (!validateToken(token)) {
                return null;
            }
            
            String username = extractUsername(token);
            return empleadoRepository.findByCorreo(username).orElse(null);
        } catch (Exception ex) {
            return null;
        }
    }
}
