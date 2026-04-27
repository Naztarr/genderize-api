package com.naz.profiler.security;

import com.naz.profiler.entity.User;
import com.naz.profiler.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String auth = request.getHeader("Authorization");

        if(auth == null || !auth.startsWith("Bearer ")) {
            filterChain.doFilter(request,response);
            return;
        }

        String token = auth.substring(7);

        if(jwtService.valid(token)) {

            UUID userId =
                    UUID.fromString(jwtService.extractUserId(token));

            User user = userRepository.findById(userId).orElse(null);

            if(user != null) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                List.of(
                                        new SimpleGrantedAuthority(
                                                "ROLE_" + user.getRole().name()
                                        )
                                )
                        );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request,response);
    }
}
