package edu.upn.proyecto.gruposowad.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                    .requestMatchers("/usuario/login", "/login/verify-mfa", "/usuario/*/mfa/setup").permitAll()
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
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
