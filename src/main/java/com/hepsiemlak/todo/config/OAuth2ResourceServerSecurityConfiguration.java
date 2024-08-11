package com.hepsiemlak.todo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * @author suleyman.yildirim
 */
@Configuration
@EnableWebSecurity
public class OAuth2ResourceServerSecurityConfiguration {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    String jwkSetUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.GET, "/v1/**").hasAuthority("SCOPE_message:read")
                        .requestMatchers(HttpMethod.POST, "/v1/**").hasAuthority("SCOPE_message:write")
                        .requestMatchers(HttpMethod.PUT, "/v1/**").hasAuthority("SCOPE_message:write")
                        .requestMatchers(HttpMethod.DELETE, "/v1/**").hasAuthority("SCOPE_message:write")
                        .requestMatchers(HttpMethod.PATCH, "/v1/**").hasAuthority("SCOPE_message:write")
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll() // Allow Swagger UI and related resources
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(withDefaults())); // Use withDefaults() for default configuration
        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
    }

}