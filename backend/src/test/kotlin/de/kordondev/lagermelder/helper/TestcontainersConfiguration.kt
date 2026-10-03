package de.kordondev.lagermelder.helper

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.postgresql.PostgreSQLContainer

/**
 * Starts a real Postgres (same major version as production, see docker-compose) for integration tests.
 * Spring Boot wires the datasource via [ServiceConnection]; Liquibase migrates the schema on startup.
 */
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {
    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer = PostgreSQLContainer("postgres:18")
}
