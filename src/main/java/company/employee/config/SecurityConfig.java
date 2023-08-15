package company.employee.config;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import company.employee.security.ApiKeyAuthenticationFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${app.http.api-key-header}")
    private String apiKeyRequestHeader;

    @Value("${app.http.api-key}")
    private String apiKeyValue;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ApiKeyAuthenticationFilter apiKeyAuthFilter)
            throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers(HttpMethod.GET, "/employees").permitAll()
                                .requestMatchers(HttpMethod.GET, "/employees/{id}").permitAll()
                                .requestMatchers(HttpMethod.POST, "/employees").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/employees/{id}").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/employees/{id}").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api-docs").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api-docs/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/swagger").permitAll()
                                .requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
                                .anyRequest().denyAll())
                .addFilter(apiKeyAuthFilter)
                .sessionManagement(sesssion -> sesssion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public ApiKeyAuthenticationFilter apiKeyAuthenticationFilter(AuthenticationManager authenticationManager) {
        ApiKeyAuthenticationFilter apiKeyAuthFilter = new ApiKeyAuthenticationFilter(apiKeyRequestHeader);
        apiKeyAuthFilter.setAuthenticationManager(authenticationManager);
        return apiKeyAuthFilter;
    }

    @Bean
    public AuthenticationManager apiKeyAuthenticationManager() {
        return authentication -> {
            Object principal = authentication.getPrincipal();
            if (!Objects.equals(apiKeyValue, principal)) {
                throw new BadCredentialsException("API key missing");
            }
            authentication.setAuthenticated(true);
            return authentication;
        };
    }
}
