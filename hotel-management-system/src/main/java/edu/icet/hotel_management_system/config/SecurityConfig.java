package edu.icet.hotel_management_system.config;

import com.stripe.Stripe;
import edu.icet.hotel_management_system.security.JwtAuthEntryPoint;
import edu.icet.hotel_management_system.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthEntryPoint jwtAuthEntryPoint;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Value("${cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    // Public endpoints that don't require authentication
    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/**",
            "/api/rooms/search",
            "/api/rooms",
            "/api/rooms/{id}",
            "/api/rooms/*/images",
            "/api/rooms/available",
            "/api/bookings/availability",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**",
            "/v3/api-docs/**",
            "/uploads/**",
            "/h2-console/**",
            "/error",
            "/actuator/health",
            "/favicon.ico"
    };

    // Admin-only endpoints
    private static final String[] ADMIN_ENDPOINTS = {
            "/api/admin/**",
            "/api/users/**",
            "/api/bookings/search",
            "/api/payments"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS Configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // CSRF disabled for REST API
                .csrf(csrf -> csrf.disable())

                // Exception handling
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthEntryPoint))

                // Session management
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Security headers
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()) // Allow H2 console
                        .referrerPolicy(referrer ->
                                referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                        .httpStrictTransportSecurity(hsts ->
                                hsts.maxAgeInSeconds(31536000).includeSubDomains(true))
                )


                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()

                        // Admin-only endpoints
                        .requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")

                        // Room management (Admin only for CUD operations)
                        .requestMatchers("POST", "/api/rooms").hasRole("ADMIN")
                        .requestMatchers("PUT", "/api/rooms/**").hasRole("ADMIN")
                        .requestMatchers("DELETE", "/api/rooms/**").hasRole("ADMIN")
                        .requestMatchers("POST", "/api/rooms/*/image").hasRole("ADMIN")
                        .requestMatchers("DELETE", "/api/rooms/*/image/**").hasRole("ADMIN")

                        // Booking management
                        .requestMatchers("GET", "/api/bookings").hasRole("ADMIN")
                        .requestMatchers("PUT", "/api/bookings/*/confirm").hasRole("ADMIN")
                        .requestMatchers("PUT", "/api/bookings/*/complete").hasRole("ADMIN")
                        .requestMatchers("DELETE", "/api/bookings/**").hasRole("ADMIN")

                        // Payment management
                        .requestMatchers("/api/payments/cash").hasAnyRole("ADMIN", "CASHIER")
                        .requestMatchers("/api/payments/statistics").hasRole("ADMIN")
                        .requestMatchers("/api/payments/search").hasRole("ADMIN")
                        .requestMatchers("/api/payments/*/refund").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/payments/*/status").hasRole("ADMIN")
                        .requestMatchers("/api/payments").hasRole("ADMIN")

                        // User management
                        .requestMatchers("POST", "/api/users").hasRole("ADMIN")
                        .requestMatchers("DELETE", "/api/users/**").hasRole("ADMIN")
                        .requestMatchers("GET", "/api/users").hasRole("ADMIN")
                        .requestMatchers("GET", "/api/users/email/**").hasRole("ADMIN")

                        // Authenticated user endpoints
                        .requestMatchers("GET", "/api/users/**").authenticated()
                        .requestMatchers("PUT", "/api/users/**").authenticated()
                        .requestMatchers("POST", "/api/users/change-password").authenticated()
                        .requestMatchers("POST", "/api/bookings").authenticated()
                        .requestMatchers("PUT", "/api/bookings/**").authenticated()
                        .requestMatchers("GET", "/api/bookings/**").authenticated()
                        .requestMatchers("POST", "/api/payments").authenticated()
                        .requestMatchers("GET", "/api/payments/**").authenticated()

                        // Role and Permission Management
                        .requestMatchers("GET", "/api/roles-permissions/my-permissions").authenticated()
                        .requestMatchers("/api/roles-permissions/**").hasRole("ADMIN")
                        // All other requests require authentication
                        .anyRequest().authenticated()
                );

        // Add JWT filter
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Parse allowed origins from configuration
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOrigins(origins.stream()
                .map(String::trim)
                .collect(java.util.stream.Collectors.toList()));

        // Allowed methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));

        // Allowed headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Allow credentials
        configuration.setAllowCredentials(true);

        // Exposed headers
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Total-Count", "X-Page-Number"
        ));

        // Max age for preflight requests
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Increased strength
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }



}