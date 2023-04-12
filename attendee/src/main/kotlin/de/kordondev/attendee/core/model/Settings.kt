package de.kordondev.attendee.core.model

import java.time.Instant
import java.time.LocalDate

data class Settings(
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
    val startDownloadRegistrationFiles: LocalDate
)