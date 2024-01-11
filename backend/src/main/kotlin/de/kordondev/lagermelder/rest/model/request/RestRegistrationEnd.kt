package de.kordondev.lagermelder.rest.model.request

import de.kordondev.lagermelder.core.model.Settings
import java.time.Instant

data class RestRegistrationEnd(
    val registrationEnd: Instant,
    val attendeesCanBeEdited: Boolean
) {
    companion object {
        fun of(settings: Settings, attendeesCanBeEdited: Boolean): RestRegistrationEnd {
            return RestRegistrationEnd(
                registrationEnd = settings.registrationEnd,
                attendeesCanBeEdited = attendeesCanBeEdited
            )
        }
    }
}