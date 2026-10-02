package de.kordondev.lagermelder.helper

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

/**
 * Marks a full Spring Boot integration test running against a Postgres Testcontainer.
 * Use this instead of a plain [SpringBootTest].
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest
@Import(TestcontainersConfiguration::class)
annotation class IntegrationTest
