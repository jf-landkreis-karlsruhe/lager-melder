package de.kordondev.lagermelder.rest.model.request

import de.kordondev.lagermelder.core.persistence.entry.SettingsEntry
import java.time.LocalDate

data class RestRegistrationEnd(
    val registrationEnd: LocalDate,
    val attendeesCanBeEdited: Boolean,
    val childGroupsRegistrationEnd: LocalDate,
    val childGroupsCanBeEdited: Boolean,
    val helpersRegistrationEnd: LocalDate,
    val helpersCanBeEdited: Boolean,
    val eventEnd: LocalDate,
) {
    companion object {
        fun of(
            settings: SettingsEntry,
            attendeesCanBeEdited: Boolean,
            childGroupsCanBeEdited: Boolean,
            helperCanBeEdited: Boolean,
        ): RestRegistrationEnd =
            RestRegistrationEnd(
                registrationEnd = settings.registrationEnd,
                attendeesCanBeEdited = attendeesCanBeEdited,
                childGroupsRegistrationEnd = settings.childGroupsRegistrationEnd,
                childGroupsCanBeEdited = childGroupsCanBeEdited,
                helpersRegistrationEnd = settings.helpersRegistrationEnd,
                helpersCanBeEdited = helperCanBeEdited,
                eventEnd = settings.eventEnd,
            )
    }
}
