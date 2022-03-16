package de.kordondev.attendee.rest.model.request

import de.kordondev.attendee.core.model.Settings
import java.time.Instant

data class RestRegistrationEnd(
    val registrationEnd: Instant,
) {
    companion object {
        fun of(settings: Settings): RestRegistrationEnd {
            return RestRegistrationEnd(registrationEnd = settings.registrationEnd)
        }
    }
}