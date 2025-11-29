package net.ryzen.paylinksystem.common.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.config.properties.JwtTokenProperties;
import net.ryzen.paylinksystem.dto.JwtUtilsResponseDTO;
import net.ryzen.paylinksystem.entity.Client;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtils {

    private final JwtTokenProperties jwtTokenProperties;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtTokenProperties.getSecretKey());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtUtilsResponseDTO generateToken(Client user) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtTokenProperties.getAccessTokenExpiry());
        return JwtUtilsResponseDTO.builder()
                .token(Jwts.builder()
                        .setSubject(user.getEmail())
                        .setIssuedAt(new Date())
                        .setExpiration(expiryDate)
                        .signWith(key)
                        .compact())
                .expiryTime(expiryDate)
                .build();
    }

    public JwtUtilsResponseDTO generateRefreshToken(Client user) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtTokenProperties.getRefreshTokenExpiry());
        return JwtUtilsResponseDTO.builder()
                .expiryTime(expiryDate)
                .token(Jwts.builder()
                        .setSubject(user.getEmail())
                        .setIssuedAt(new Date())
                        .setExpiration(expiryDate)
                        .signWith(key)
                        .compact())
                .build();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}