package de.kordondev.lagermelder.rest.model.request

import de.kordondev.lagermelder.core.persistence.entry.SettingsEntry
import java.time.Instant

data class RestRegistrationEnd(
    val registrationEnd: Instant,
    val attendeesCanBeEdited: Boolean,
    val childGroupsRegistrationEnd: Instant,
    val childGroupsCanBeEdited: Boolean,
    val helpersRegistrationEnd: Instant,
    val helpersCanBeEdited: Boolean,
) {
    companion object {
        fun of(
            settings: SettingsEntry,
            attendeesCanBeEdited: Boolean,
            childGroupsCanBeEdited: Boolean,
            helperCanBeEdited: Boolean
        ): RestRegistrationEnd {
            return RestRegistrationEnd(
                registrationEnd = settings.registrationEnd,
                attendeesCanBeEdited = attendeesCanBeEdited,
                childGroupsRegistrationEnd = settings.childGroupsRegistrationEnd,
                childGroupsCanBeEdited = childGroupsCanBeEdited,
                helpersRegistrationEnd = settings.helpersRegistrationEnd,
                helpersCanBeEdited = helperCanBeEdited
            )
        }
    }
}