package com.soutenance.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soutenance.security.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) ->
                                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", "Authentification requise"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeError(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden", "Acces refuse")))
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/refresh", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/api/chatbot/ping").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/me")
                        .hasAnyRole("ADMIN", "ENSEIGNANT", "ETUDIANT")
                        .requestMatchers(HttpMethod.GET, "/api/enseignants/me", "/api/encadrants/me")
                        .hasRole("ENSEIGNANT")
                        .requestMatchers(HttpMethod.GET, "/api/etudiants/me", "/api/resultats/me")
                        .hasRole("ETUDIANT")
                        .requestMatchers(HttpMethod.GET, "/api/soutenances", "/api/soutenances/*", "/api/soutenances/etudiant/**")
                        .hasAnyRole("ADMIN", "ENSEIGNANT", "ETUDIANT")
                        .requestMatchers(HttpMethod.GET, "/api/resultats/etudiant/**")
                        .hasAnyRole("ADMIN", "ENSEIGNANT", "ETUDIANT")
                        .requestMatchers(HttpMethod.GET, "/api/resultats/soutenance/**", "/api/resultats/published", "/api/resultats/publies")
                        .hasAnyRole("ADMIN", "ENSEIGNANT")
                        .requestMatchers(HttpMethod.POST, "/api/chatbot/ask")
                        .hasAnyRole("ADMIN", "ENSEIGNANT", "ETUDIANT")
                        .requestMatchers(HttpMethod.GET, "/api/notes/soutenance/**")
                        .hasAnyRole("ADMIN", "ENSEIGNANT")
                        .requestMatchers(HttpMethod.POST, "/api/notes/**")
                        .hasAnyRole("ADMIN", "ENSEIGNANT")
                        .requestMatchers(HttpMethod.PUT, "/api/notes/**")
                        .hasAnyRole("ADMIN", "ENSEIGNANT")
                        .requestMatchers(HttpMethod.GET, "/api/etudiants/*")
                        .hasAnyRole("ADMIN", "ENSEIGNANT", "ETUDIANT")
                        .requestMatchers(HttpMethod.GET, "/api/resultats", "/api/resultats/*", "/api/etudiants", "/api/enseignants/**", "/api/encadrants/**", "/api/salles/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/api/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:[*]",
                "http://127.0.0.1:[*]",
                "http://192.168.*.*:[*]",
                "http://10.*.*.*:[*]",
                "http://172.16.*.*:[*]",
                "http://172.17.*.*:[*]",
                "http://172.18.*.*:[*]",
                "http://172.19.*.*:[*]",
                "http://172.20.*.*:[*]",
                "http://172.21.*.*:[*]",
                "http://172.22.*.*:[*]",
                "http://172.23.*.*:[*]",
                "http://172.24.*.*:[*]",
                "http://172.25.*.*:[*]",
                "http://172.26.*.*:[*]",
                "http://172.27.*.*:[*]",
                "http://172.28.*.*:[*]",
                "http://172.29.*.*:[*]",
                "http://172.30.*.*:[*]",
                "http://172.31.*.*:[*]"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void writeError(HttpServletResponse response, int status, String error, String message) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
