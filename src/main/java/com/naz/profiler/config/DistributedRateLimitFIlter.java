package com.naz.profiler.config;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class DistributedRateLimitFIlter implements Filter {
    private final ProxyManager<byte[]> proxyManager;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String ip = httpRequest.getRemoteAddr(); // Use user ID from JWT if available
        Supplier<BucketConfiguration> configSupplier = getBucketConfigurationSupplier(httpRequest);

        // Get or create the bucket from Redis
        var bucket = proxyManager.builder().build(ip.getBytes(), configSupplier);

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            httpResponse.setStatus(429);
            httpResponse.setHeader("X-Rate-Limit-Retry-After-Seconds", "60");
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"status\": \"error\", \"message\": \"Too many requests. Please try again later.\"}");
        }
    }

    private static Supplier<BucketConfiguration> getBucketConfigurationSupplier(HttpServletRequest httpRequest) {
        String path = httpRequest.getRequestURI();

        // Define configurations
        Supplier<BucketConfiguration> configSupplier = () -> {
            if (path.startsWith("/auth/")) {
                return BucketConfiguration.builder()
                        .addLimit(l -> l.capacity(10).refillIntervally(10, Duration.ofMinutes(1)))
                        .build();
            }
            return BucketConfiguration.builder()
                    .addLimit(l -> l.capacity(60).refillIntervally(60, Duration.ofMinutes(1)))
                    .build();
        };
        return configSupplier;
    }
}
