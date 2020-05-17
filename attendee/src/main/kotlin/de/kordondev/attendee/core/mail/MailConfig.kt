package de.kordondev.attendee.core.mail

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix="mail")
data class MailConfig (
    var from: String = "",
    var host: String = "",
    var port: Number = 0,
    var username: String = "",
    var password: String = ""
)