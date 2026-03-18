package com.kunal.admission.config;

import com.kunal.admission.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // enables @preAuthorize on controller method
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter){
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF — we use JWT (stateless), not sessions
            .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Fully public - no token needed
                    .requestMatchers("/courses").permitAll()
                    .requestMatchers("/visitor", "/visitor/**").permitAll()
                    .requestMatchers("/auth/register", "/auth/login").permitAll()

                // -- Swagger
                .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v2/api-docs/**",
                        "/v3/api-docs/**",
                        "/webjars/**"
                ).permitAll()

                //- Admin only
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                  // Admitted students only
                    .requestMatchers("/erp/**").hasRole("STUDENT")
                    // applicants
                    .requestMatchers("/application/**","/document/**").hasAnyRole("APPLICANT", "STUDENT", "ADMIN")

                    // Everything else requires a valid token
                .anyRequest().authenticated()
            )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}





//    http
//            .csrf(csrf -> csrf.disable())
//        .authorizeHttpRequests(auth -> auth
//        .requestMatchers(
//                        "/visitor","/visitor/**"
//).permitAll()
//                .requestMatchers("/swagger-ui/**", "/swagger-resources/**", "/v2/api-docs/**", "/webjars/**").permitAll()
//                .anyRequest().authenticated()
//            );