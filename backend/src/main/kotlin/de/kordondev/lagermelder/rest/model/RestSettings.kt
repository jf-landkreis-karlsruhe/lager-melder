package de.kordondev.lagermelder.rest.model

import de.kordondev.lagermelder.core.persistence.entry.SettingsEntry
import java.time.Instant
import java.time.LocalDate

data class RestSettings(
    val id: Long,
    val registrationEnd: Instant,
    val hostCity: String,
    val eventStart: LocalDate,
    val eventEnd: LocalDate,
    val eventName: String,
    val eventAddress: String,
    val organizer: String,
    val organisationAddress: String, // Multiline
    val moneyPerYouthLoader: String,
    val startDownloadRegistrationFiles: Instant,
    val childGroupsRegistrationEnd: Instant,
    val helpersRegistrationEnd: Instant,
    val numberOfDuties: Int
) {
    companion object {
        fun of(settings: SettingsEntry) = RestSettings(
            id = settings.id,
            registrationEnd = settings.registrationEnd,
            hostCity = settings.hostCity,
            eventStart = settings.eventStart,
            eventEnd = settings.eventEnd,
            eventName = settings.eventName,
            eventAddress = settings.eventAddress,
            organizer = settings.organizer,
            organisationAddress = settings.organisationAddress,
            moneyPerYouthLoader = settings.moneyPerYouthLoader,
            startDownloadRegistrationFiles = settings.startDownloadRegistrationFiles,
            childGroupsRegistrationEnd = settings.childGroupsRegistrationEnd,
            helpersRegistrationEnd = settings.helpersRegistrationEnd,
            numberOfDuties = settings.numberOfDuties
        )

        fun to(settings: RestSettings) = SettingsEntry(
            id = settings.id,
            registrationEnd = settings.registrationEnd,
            hostCity = settings.hostCity,
            eventStart = settings.eventStart,
            eventEnd = settings.eventEnd,
            eventName = settings.eventName,
            eventAddress = settings.eventAddress,
            organizer = settings.organizer,
            organisationAddress = settings.organisationAddress,
            moneyPerYouthLoader = settings.moneyPerYouthLoader,
            startDownloadRegistrationFiles = settings.startDownloadRegistrationFiles,
            childGroupsRegistrationEnd = settings.childGroupsRegistrationEnd,
            helpersRegistrationEnd = settings.helpersRegistrationEnd,
            numberOfDuties = settings.numberOfDuties
        )
    }
}
