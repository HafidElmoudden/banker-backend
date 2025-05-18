package com.hafidelmoudden.bankerbackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> {})
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - both with and without /api prefix
                .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh-token").permitAll()
                .requestMatchers("/auth/login", "/auth/register", "/auth/refresh-token").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Swagger UI endpoints
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/v3/api-docs/**").permitAll()
                
                // Protected endpoints - require authentication
                .requestMatchers("/api/auth/change-password").authenticated()
                .requestMatchers("/auth/change-password").authenticated()
                
                // Dashboard statistics endpoints, specific endpoints for both USER and ADMIN
                .requestMatchers(HttpMethod.GET, "/api/dashboard/statistics").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/dashboard/statistics").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/statistics").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/statistics").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/dashboard/operations/monthly").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/dashboard/operations/monthly").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/dashboard/accounts/distribution").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/dashboard/accounts/distribution").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/dashboard/transactions/recent").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/dashboard/transactions/recent").hasAnyRole("USER", "ADMIN")
                
                // User endpoints it requires USER or ADMIN role
                .requestMatchers(HttpMethod.GET, "/api/customers/profile").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/customers/profile").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/accounts/user/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/accounts/user/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/operations/user/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/operations/user/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/dashboard/user/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/dashboard/user/**").hasAnyRole("USER", "ADMIN")
                
                // Admin endpoints it requires ADMIN role
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/customers/**").hasRole("ADMIN")
                .requestMatchers("/customers/**").hasRole("ADMIN")
                .requestMatchers("/api/accounts/**").hasRole("ADMIN")
                .requestMatchers("/accounts/**").hasRole("ADMIN")
                .requestMatchers("/api/operations/**").hasRole("ADMIN")
                .requestMatchers("/operations/**").hasRole("ADMIN")
                .requestMatchers("/api/dashboard/**").hasRole("ADMIN")
                .requestMatchers("/dashboard/**").hasRole("ADMIN")
                
                // Any other request needs authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
} 