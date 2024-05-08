package de.kordondev.lagermelder.rest.model.request;

import de.kordondev.lagermelder.core.persistence.entry.SettingsEntry
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant
import java.time.LocalDate

data class RestSettingsRequest(
    @NotNull
    @FutureOrPresent
    val registrationEnd: Instant,

    @NotNull
    @NotBlank
    val hostCity: String,

    @NotNull
    val eventStart: LocalDate,

    @NotNull
    val eventEnd: LocalDate,

    @NotNull
    @NotBlank
    val eventName: String,

    @NotNull
    @NotBlank
    val eventAddress: String,

    @NotNull
    @NotBlank
    val organizer: String,

    @NotNull
    @NotBlank
    val organisationAddress: String, // Multiline

    @NotNull
    @NotBlank
    val moneyPerYouthLoader: String,

    @NotNull
    @NotBlank
    val startDownloadRegistrationFiles: Instant
) {
    companion object {
        fun to(settings: RestSettingsRequest) = SettingsEntry(
            id = 0,
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
