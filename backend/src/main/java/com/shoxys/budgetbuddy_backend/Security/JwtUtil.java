package com.shoxys.budgetbuddy_backend.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static com.shoxys.budgetbuddy_backend.Config.Constants.JWT_EXPIRATION_DAYS;

/**
 * Utility class for generating, parsing, and validating JWT tokens.
 */
@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String jwtSecret;

  /**
   * Generates a JWT token for the specified user.
   *
   * @param userDetails the user details
   * @return the generated JWT token
   */
  public String generateToken(UserDetails userDetails) {
    return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(Date.from(Instant.now().plus(JWT_EXPIRATION_DAYS, ChronoUnit.DAYS)))
            .signWith(SignatureAlgorithm.HS256, jwtSecret)
            .compact();
  }

  /**
   * Extracts the username (email) from a JWT token.
   *
   * @param token the JWT token
   * @return the username (email) from the token
   */
  public String extractUsername(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

  /**
   * Validates a JWT token against user details.
   *
   * @param token       the JWT token
   * @param userDetails the user details
   * @return true if the token is valid and matches the user, false otherwise
   */
  public boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return username.equals(userDetails.getUsername());
  }
}