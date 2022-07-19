package com.example.user_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This is util class to generate and validate token
 */
@Service
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

//    @Value("${project.jwt.secretkey}")
    private static final String secretKey="medicineadherencesecretkey";

    @Value("${project.jwt.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String extractUsername(String token) {
            return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String userId) {
        logger.info("Generating token");
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userId);
    }

    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    public String generateRefreshToken(String userId) {
        logger.info("Generating token");
        Map<String, Object> claims = new HashMap<>();
        return createRefreshToken(claims, userId);
    }

    private String createRefreshToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 40))
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }
    public Boolean validateToken(String token, UserDetails userDetails ) {
        logger.info("Validating token");
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()));

    }
}