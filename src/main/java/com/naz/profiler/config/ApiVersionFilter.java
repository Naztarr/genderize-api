package com.naz.profiler.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiVersionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (path.startsWith("/api")) {

            String version =
                    request.getHeader("X-API-Version");

            if (version == null || !"1".equals(version)) {
                String errorMsg = (version == null) ? "X-API-Version header is missing" : "Unsupported API version";

                response.setStatus(400);
                response.setContentType("application/json");

                response.getWriter().write(String.format("""
                {
                  "status":"error",
                  "message":"%s"
                }
                """, errorMsg));

                return;
            }
        }

        filterChain.doFilter(request,response);

    }
}
