package com.kunal.admission.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/visitor",
                        "/visitor/**",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v2/api-docs/**",
                        "/v3/api-docs/**",
                        "/webjars/**"
                ).permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
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