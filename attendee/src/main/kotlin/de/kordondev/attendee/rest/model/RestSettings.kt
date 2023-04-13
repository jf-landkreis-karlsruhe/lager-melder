package de.kordondev.attendee.rest.model

import de.kordondev.attendee.core.model.Settings
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
    val startDownloadRegistrationFiles: Instant
) {
    companion object {
        fun of(settings: Settings) = RestSettings(
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
            startDownloadRegistrationFiles = settings.startDownloadRegistrationFiles
        )

        fun to(settings: RestSettings) = Settings(
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
            startDownloadRegistrationFiles = settings.startDownloadRegistrationFiles
        )
    }
}
