package de.kordondev.lagermelder.rest

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.cfg.EnumFeature

/**
 * Jackson 3 (Spring Boot 4) reads and writes enums via toString() by default. Food and DepartmentFeatures override
 * toString() with German display texts, but the frontend exchanges the enum names (e.g. MEAT, YOUTH_GROUPS).
 */
@Configuration
class JacksonConfiguration {
    @Bean
    fun enumsByNameCustomizer() =
        JsonMapperBuilderCustomizer { builder ->
            builder
                .disable(EnumFeature.READ_ENUMS_USING_TO_STRING)
                .disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
        }
}
