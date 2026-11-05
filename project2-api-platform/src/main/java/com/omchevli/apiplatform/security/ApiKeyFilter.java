package com.omchevli.apiplatform.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Guards /api/internal/** with a shared API key in the X-Api-Key header.
 * The key comes from the INTERNAL_API_KEY environment variable
 * (default "dev-internal-key" for local runs only).
 */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private final String expectedKey;

    public ApiKeyFilter(@Value("${app.internal-api-key:dev-internal-key}") String expectedKey) {
        this.expectedKey = expectedKey;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/internal/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String key = request.getHeader("X-Api-Key");
        if (key == null || !key.equals(expectedKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"missing or invalid X-Api-Key\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
