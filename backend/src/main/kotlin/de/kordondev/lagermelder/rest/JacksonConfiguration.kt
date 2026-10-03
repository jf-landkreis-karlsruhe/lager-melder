package de.kordondev.lagermelder.rest

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.cfg.EnumFeature

/**
 * Restores Jackson 2 defaults the frontend relies on, changed by Jackson 3 (Spring Boot 4):
 * - enums are read and written via toString(). Food and DepartmentFeatures override toString() with German display
 *   texts, but the frontend exchanges the enum names (e.g. MEAT, YOUTH_GROUPS).
 * - missing primitive properties fail. The frontend omits e.g. partOfDepartmentId for attendees that are no Z-Kids.
 */
@Configuration
class JacksonConfiguration {
    @Bean
    fun enumsByNameCustomizer() =
        JsonMapperBuilderCustomizer { builder ->
            builder
                .disable(EnumFeature.READ_ENUMS_USING_TO_STRING)
                .disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
        }
}
