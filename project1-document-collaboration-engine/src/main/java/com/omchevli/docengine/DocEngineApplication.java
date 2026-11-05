package com.omchevli.docengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Intelligent Enterprise Document &amp; Collaboration Engine.
 *
 * Runs with an in-memory document store by default (profile "dev", the default),
 * so it starts anywhere with no external services. Activate the "mongo" profile
 * (with the docker-compose MongoDB, or any MongoDB URI) to persist documents in
 * MongoDB via Spring Data instead.
 */
@SpringBootApplication
public class DocEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(DocEngineApplication.class, args);
    }
}
