package de.kordondev.lagermelder.rest.model.request

import de.kordondev.lagermelder.core.persistence.entry.SettingsEntry
import java.time.LocalDate

data class RestStartDownloadRegistrationFiles(
    val startDownloadRegistrationFiles: LocalDate,
    val registrationFilesCanBeDownloaded: Boolean,
) {
    companion object {
        fun of(
            settings: SettingsEntry,
            registrationFilesCanBeDownloaded: Boolean,
        ) = RestStartDownloadRegistrationFiles(
            startDownloadRegistrationFiles = settings.startDownloadRegistrationFiles,
            registrationFilesCanBeDownloaded = registrationFilesCanBeDownloaded,
        )
    }
}
