package com.campus.feedbacktool.config;

import com.campus.feedbacktool.security.AppUserDetailsService;
import com.campus.feedbacktool.security.JwtAccessDeniedHandler;
import com.campus.feedbacktool.security.JwtAuthFilter;
import com.campus.feedbacktool.security.JwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

/**
 * Authorization model - only admins can ever log in (no public registration),
 * so every protected route below is effectively "admin only":
 *
 *   - POST /api/auth/login                          -> public (the only auth endpoint)
 *   - GET  /api/surveys, /api/surveys/{id}           -> public (anyone can view a survey's question)
 *   - POST /api/responses                             -> public (anyone can submit a response, no login)
 *   - POST/PUT/DELETE /api/surveys/**                 -> ADMIN only (manage surveys)
 *   - GET  /api/surveys/{id}/responses                -> ADMIN only (view a survey's responses)
 *   - GET  /api/surveys/{id}/stats                    -> ADMIN only (aggregate stats over responses)
 *   - GET  /api/responses, GET /api/responses/{id}    -> ADMIN only (view responses)
 *   - PUT/DELETE /api/responses/**                    -> ADMIN only (edit/remove a response)
 *   - H2 console                                        -> public (dev convenience only)
 * Sessions are stateless - every request must carry its own JWT.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AppUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(AppUserDetailsService userDetailsService,
                           JwtAuthFilter jwtAuthFilter,
                           JwtAuthenticationEntryPoint authenticationEntryPoint,
                           JwtAccessDeniedHandler accessDeniedHandler) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                // H2 console renders in a frame; only relax this for dev convenience.
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        // CORS preflight must always be allowed through.
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // Public: anyone can view a survey's title/question.
                        .requestMatchers(HttpMethod.GET, "/api/surveys").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/surveys/{id}").permitAll()

                        // Public: anyone can submit a response - no login required.
                        .requestMatchers(HttpMethod.POST, "/api/responses").permitAll()

                        // Admin only: manage surveys.
                        .requestMatchers(HttpMethod.POST, "/api/surveys/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/surveys/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/surveys/**").hasRole("ADMIN")

                        // Admin only: viewing responses (individually, per-survey, or their stats).
                        .requestMatchers(HttpMethod.GET, "/api/surveys/*/responses").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/surveys/*/stats").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/responses/**").hasRole("ADMIN")

                        // Admin only: editing/removing a response after submission.
                        .requestMatchers(HttpMethod.PUT, "/api/responses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/responses/**").hasRole("ADMIN")

                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
