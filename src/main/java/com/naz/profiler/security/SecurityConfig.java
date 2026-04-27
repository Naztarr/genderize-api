package com.naz.profiler.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())

                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/**")
                        .hasAnyRole("ADMIN","ANALYST")
                        .requestMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}


//@Bean
//SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//    http
//            .csrf(csrf -> csrf.disable())
//            .cors(Customizer.withDefaults())
//
//            .sessionManagement(s ->
//                    s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//
//            .authorizeHttpRequests(auth -> auth
//                    .requestMatchers("/auth/**").permitAll()
//                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//
//                    .requestMatchers(HttpMethod.GET, "/api/**")
//                    .hasAnyRole("ADMIN","ANALYST")
//
//                    .requestMatchers(
//                            HttpMethod.POST, "/api/**").hasRole("ADMIN")
//                    .requestMatchers(
//                            HttpMethod.PUT, "/api/**").hasRole("ADMIN")
//                    .requestMatchers(
//                            HttpMethod.PATCH, "/api/**").hasRole("ADMIN")
//                    .requestMatchers(
//                            HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
//
//                    .anyRequest().authenticated()
//            )
//
//            .exceptionHandling(ex -> ex
//                    .authenticationEntryPoint((req,res,e) -> {
//                        res.setStatus(401);
//                        res.setContentType("application/json");
//                        res.getWriter().write(
//                                "{\"status\":\"error\",\"message\":\"Unauthorized\"}");
//                    })
//                    .accessDeniedHandler((req,res,e) -> {
//                        res.setStatus(403);
//                        res.setContentType("application/json");
//                        res.getWriter().write(
//                                "{\"status\":\"error\",\"message\":\"Forbidden\"}");
//                    })
//            )
//
//            .addFilterBefore(jwtAuthFilter,
//                    UsernamePasswordAuthenticationFilter.class);
//
//    return http.build();
//}
