package com.shoxys.budgetbuddy_backend.Config;

import com.shoxys.budgetbuddy_backend.Security.AppUserDetailsService;
import com.shoxys.budgetbuddy_backend.Security.JwtAuthFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
  private final AppUserDetailsService userDetailsService;
  private final JwtAuthFilter jwtAuthFilter;

  @Value("${cors.allowed-origins:http://localhost:5173}")
  private String allowedOrigins;

  public SecurityConfig(AppUserDetailsService userDetailsService, JwtAuthFilter jwtAuthFilter) {
    this.userDetailsService = userDetailsService;
    this.jwtAuthFilter = jwtAuthFilter;
    logger.info("Initializing SecurityConfig with allowed origins: {}", allowedOrigins);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
    logger.info("Configuring SecurityFilterChain");
    http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/**").permitAll()
                    .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationManager(authenticationManager)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    logger.debug("SecurityFilterChain configured successfully");
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    logger.debug("Configuring CORS with allowed origins: {}", allowedOrigins);
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(allowedOrigins.split(",")));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    logger.debug("CORS configuration completed");
    return source;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    logger.debug("Creating BCryptPasswordEncoder");
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    logger.debug("Creating AuthenticationManager");
    return authenticationConfiguration.getAuthenticationManager();
  }
}