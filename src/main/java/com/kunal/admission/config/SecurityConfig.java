package com.kunal.admission.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // enables @PreAuthorize on controller methods
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter){
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Allow the browser frontend (Vite dev server, etc.) to call us cross-origin.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Disable CSRF — we use JWT (stateless), not sessions
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Fully public — no token needed
                        .requestMatchers("/courses").permitAll()
                        .requestMatchers("/auth/register", "/auth/login").permitAll()

                        // Requirement 1: online enquiry form is public.
                        // Only POST /visitor is open to the world.
                        .requestMatchers(HttpMethod.POST, "/visitor").permitAll()

                        // Any logged-in user can fetch their own visitor record
                        .requestMatchers(HttpMethod.GET, "/visitor/me").authenticated()

                        // Swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/v2/api-docs/**",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        ).permitAll()

                        // Admin-only areas
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Visitor management (list, get, update, delete, status change) is admin-only
                        .requestMatchers("/visitor", "/visitor/**").hasRole("ADMIN")
                        // Admitted students only
                        .requestMatchers("/erp/**").hasRole("STUDENT")
                        // Applicants and above
                        .requestMatchers("/application/**", "/document/**").hasAnyRole("APPLICANT", "STUDENT", "ADMIN")

                        // Everything else requires a valid token
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS configuration so the React dev server (Vite on 5173, CRA on 3000) and
     * any locally-served static frontend can call this API. Tighten the origin
     * list when deploying to production.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost:3000",
                "http://127.0.0.1:3000"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
