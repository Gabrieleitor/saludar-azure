package com.gabrieleitor.saludar.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter)
            throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/saludar2/**").authenticated()
                        .anyRequest().permitAll())
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new EntraRoleAuthoritiesConverter());
        return converter;
    }

    private static final class EntraRoleAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            Object rolesClaim = jwt.getClaims().get("roles");

            if (rolesClaim instanceof Collection<?> roles) {
                for (Object role : roles) {
                    if (role != null) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                    }
                }
            }

            Object scpClaim = jwt.getClaims().get("scp");
            if (scpClaim instanceof String scp && !scp.isBlank()) {
                for (String scope : scp.split(" ")) {
                    if (!scope.isBlank()) {
                        authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope));
                    }
                }
            } else if (scpClaim instanceof Collection<?> scopes) {
                for (Object scope : scopes) {
                    if (scope != null && !scope.toString().isBlank()) {
                        authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope));
                    }
                }
            }

            return authorities.stream().filter(Objects::nonNull).toList();
        }
    }
}
