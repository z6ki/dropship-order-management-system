package _8.cscu9yw.ordermanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Collections;

@Configuration
public class SecurityConfig {

    // Load API key from application properties
    @Value("${operator.api.key}")
    private String operatorApiKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        	// Disable CSRF protection for H2 console and disable it globally for it
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**").disable())
            // Allow H2 console to be displayed in a frame 
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            // Configure endpoint authorization
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/api/operator/**").authenticated()// Secure operator API endpoints
                .requestMatchers("/h2-console/**").permitAll()// Allow unrestricted access to H2 console
                .anyRequest().permitAll()
            )
            // Add custom filter for API key checking
            .addFilterBefore((request, response, chain) -> {
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                String path = httpRequest.getRequestURI();
                if (path.startsWith("/api/operator")) {
                    // Check for valid API key in header for operator endpoints
                    String apiKeyHeader = httpRequest.getHeader("X-API-KEY");
                    if (apiKeyHeader == null || !apiKeyHeader.equals(operatorApiKey)) {
                        httpResponse.setStatus(401);
                        httpResponse.getWriter().write("Unauthorized: Missing or invalid API key");
                        return; // Stop filter chain if unauthorized
                    }
                    // Mark request as authenticated if correct
                    SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken("operator", null, Collections.emptyList())
                    );
                }
                // Continue with the filter chain
                chain.doFilter(request, response);
            }, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
            // Use stateless session management
            .sessionManagement(session -> session.sessionCreationPolicy(
                org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
            );
        // Build and return the security filter chain
        return http.build();
    }
}