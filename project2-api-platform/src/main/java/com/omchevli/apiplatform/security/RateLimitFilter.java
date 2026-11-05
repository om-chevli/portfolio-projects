package com.omchevli.apiplatform.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple fixed-window rate limiter for the public API:
 * at most {@code app.public-rate-limit} requests per minute per client IP.
 * (A production deployment would back this with Redis; the interface — reject
 * with 429 + JSON error — is the part clients depend on.)
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final int limitPerMinute;
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    private record Window(long minute, int count) {}

    public RateLimitFilter(@Value("${app.public-rate-limit:100}") int limitPerMinute) {
        this.limitPerMinute = limitPerMinute;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/public/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String ip = request.getRemoteAddr();
        long minute = System.currentTimeMillis() / 60_000;
        Window w = windows.compute(ip, (k, old) ->
                (old == null || old.minute() != minute)
                        ? new Window(minute, 1)
                        : new Window(minute, old.count() + 1));
        if (w.count() > limitPerMinute) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"rate limit exceeded\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
