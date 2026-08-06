package de.kordondev.lagermelder.core.juleika

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "application.juleika")
class JuleikaConfig {
    var token: String = ""
    var useMock: Boolean = false
}
