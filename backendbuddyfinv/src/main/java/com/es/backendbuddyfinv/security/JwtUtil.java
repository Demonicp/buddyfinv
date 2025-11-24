package com.es.backendbuddyfinv.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    

    @Value("${jwt.secret}")
    private String secret;

    private final long expirationMs = 86400000; 

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }


    public String generateToken(String username, String rol, Long idUsuario, Long idAdministrador) {
        return Jwts.builder()
                .setSubject(username)
                .claim("rol", rol).claim("idUsuario", idUsuario)
                .claim("id_Administrador", idAdministrador) 
                
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Long extraerIdAdministrador(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    
        return claims.get("id_Administrador", Long.class);
    }

    public Long getIdAdministrador(String token){
        Long idAdmin = extraerIdAdministrador(token);

        if(idAdmin == null){
            idAdmin = extraerIdUsuario(token);
        }
        return idAdmin;
    }
 
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }


    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }


    public String extractRol(String token) {
        return getClaims(token).get("rol", String.class); // 👈 Extraemos el claim "rol"
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long extraerIdUsuario(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    
        return claims.get("idUsuario", Long.class);
    }




}


