package com.shoxys.budgetbuddy_backend.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
  @Value("${jwt.secret}")
  private String jwtSecret;

  public String generateToken(UserDetails userDetails) {
    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(Date.from(Instant.now().plus(7, ChronoUnit.DAYS)))
        .signWith(SignatureAlgorithm.HS256, jwtSecret)
        .compact();
  }

  public String extractUsername(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return username.equals(userDetails.getUsername());
  }
}
