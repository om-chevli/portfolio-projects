package com.omchevli.apiplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Public &amp; Internal REST API Platform.
 *
 * One service, two audiences:
 *  - /api/public/v1/**   versioned public API (web/mobile clients), rate-limited,
 *                        returns public DTOs that never expose internal fields.
 *  - /api/internal/v1/** internal API, requires the X-Api-Key header, returns full records.
 * OpenAPI docs at /swagger-ui.html.
 */
@SpringBootApplication
public class ApiPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiPlatformApplication.class, args);
    }
}
