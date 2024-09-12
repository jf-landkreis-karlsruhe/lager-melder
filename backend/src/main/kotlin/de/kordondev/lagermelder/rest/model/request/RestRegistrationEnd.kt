package de.kordondev.lagermelder.rest.model.request

import de.kordondev.lagermelder.core.persistence.entry.SettingsEntry
import java.time.Instant

data class RestRegistrationEnd(
    val registrationEnd: Instant,
    val attendeesCanBeEdited: Boolean,
    val childGroupRegistrationEnd: Instant,
    val childGroupsCanBeEdited: Boolean
) {
    companion object {
        fun of(settings: SettingsEntry, attendeesCanBeEdited: Boolean, childGroupsCanBeEdited: Boolean): RestRegistrationEnd {
            return RestRegistrationEnd(
                registrationEnd = settings.registrationEnd,
                attendeesCanBeEdited = attendeesCanBeEdited,
                childGroupRegistrationEnd = settings.childGroupsRegistrationEnd,
                childGroupsCanBeEdited = childGroupsCanBeEdited
            )
        }
    }
}