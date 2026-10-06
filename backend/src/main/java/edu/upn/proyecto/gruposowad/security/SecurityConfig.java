package edu.upn.proyecto.gruposowad.security;

import java.util.List;
import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    @Value("${app.cors.allowed-origins:http://localhost:4200}")
    private String allowedOrigins;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter){
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
        return http
                .csrf(csrf->csrf.disable())
                .cors(cors->cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->auth
                    .requestMatchers("/", "/index.html", "/favicon.ico", "/health", "/assets/**", "/*.js", "/*.css").permitAll()
                    .requestMatchers("/login", "/mfa", "/pos", "/admin", "/app/**").permitAll()
                    .requestMatchers("/usuario/login", "/login/verify-mfa").permitAll()
                    .requestMatchers("/usuario/*/mfa/setup").hasRole("ADMIN")
                    .requestMatchers("/venta/*/anular").hasRole("ADMIN")
                    .requestMatchers("/venta/*/documento").hasAnyRole("ADMIN","CAJERO")
                    .requestMatchers("/venta/pos","/caja/**","/boleta/**").hasAnyRole("ADMIN","CAJERO")
                    .requestMatchers("/producto").hasAnyRole("ADMIN", "CAJERO")
                        .requestMatchers("/producto/**").hasRole("ADMIN")
                        .requestMatchers("/categoria/**", "/proveedor/**", "/empresa/**").hasRole("ADMIN")
                        .requestMatchers("/movimientoinventario/**", "/anulacion_venta/**").hasRole("ADMIN")
                        .requestMatchers("/dashboard/**").hasAnyRole("ADMIN", "CAJERO")
                        .requestMatchers("/reportes/**").hasRole("ADMIN")
                        .requestMatchers("/empresa/**").hasRole("ADMIN")
                        .requestMatchers("/movimientoinventario/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                         
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
