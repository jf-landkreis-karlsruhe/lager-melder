package de.kordondev.attendee.rest.model

import de.kordondev.attendee.core.model.Settings
import java.time.Instant

data class RestSettings(
    val id: Long,
    val registrationEnd: Instant
) {
    companion object {
        fun of(settings: Settings) = RestSettings(
            id = settings.id,
            registrationEnd = settings.registrationEnd
        )

        fun to(settings: RestSettings) = Settings(
            id = settings.id,
            registrationEnd = settings.registrationEnd
        )
    }
}
