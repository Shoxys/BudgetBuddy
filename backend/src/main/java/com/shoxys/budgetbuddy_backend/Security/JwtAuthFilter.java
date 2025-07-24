package com.shoxys.budgetbuddy_backend.Security;

import static com.shoxys.budgetbuddy_backend.Config.Constants.JWT_HEADER;
import static com.shoxys.budgetbuddy_backend.Config.Constants.JWT_PREFIX;

import com.shoxys.budgetbuddy_backend.Config.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Filter for authenticating requests using JWT tokens from cookies or Authorization headers. */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final AppUserDetailsService userDetailsService;

  /**
   * Constructs a {@code JwtAuthFilter} with the specified JWT utility and user details service.
   *
   * @param jwtUtil the JWT utility for token operations
   * @param userDetailsService the service for loading user details
   */
  public JwtAuthFilter(JwtUtil jwtUtil, AppUserDetailsService userDetailsService) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
  }

  /**
   * Filters requests to authenticate using JWT tokens from cookies or Authorization headers.
   *
   * @param request the HTTP request
   * @param response the HTTP response
   * @param filterChain the filter chain
   * @throws ServletException if a servlet error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String jwt = null;

    // Check cookies first
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (Constants.JWT_COOKIE_NAME.equals(cookie.getName())) {
          jwt = cookie.getValue();
          break;
        }
      }
    }

    // Fallback to Authorization header
    if (jwt == null) {
      String authHeader = request.getHeader(JWT_HEADER);
      if (authHeader != null && authHeader.startsWith(JWT_PREFIX)) {
        jwt = authHeader.substring(7);
      }
    }

    if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      String email = jwtUtil.extractUsername(jwt);
      if (email != null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (jwtUtil.validateToken(jwt, userDetails)) {
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }
    }

    filterChain.doFilter(request, response);
  }
}
