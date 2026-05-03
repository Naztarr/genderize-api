package com.naz.profiler.security;

import com.naz.profiler.entity.User;
import com.naz.profiler.exception.DisabledException;
import com.naz.profiler.repository.RefreshTokenRepository;
import com.naz.profiler.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String token = recoverToken(request);

        // If no token was found anywhere, just continue the filter chain
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if(jwtService.valid(token)) {

            UUID userId =
                    UUID.fromString(jwtService.extractUserId(token));

            User user = userRepository.findById(userId).orElse(null);

            if(user != null) {
                // This ensures that if they clicked 'logout', this token is now useless.
                boolean hasActiveSession = refreshTokenRepository.existsByUserAndAndRevokedIsFalse(user);
                if (!hasActiveSession) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return; // Block the request immediately
                }

                if (!user.getIsActive()) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("""
                        {
                          "status":"error",
                          "message":"Account disabled"
                        }
                    """);
                    return;
                }

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

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return token.isBlank() ? null : token;
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
