package de.kordondev.lagermelder.rest.model.request

import de.kordondev.lagermelder.core.persistence.entry.SettingsEntry
import java.time.Instant

data class RestStartDownloadRegistrationFiles(
    val startDownloadRegistrationFiles: Instant,
    val registrationFilesCanBeDownloaded: Boolean
) {
    companion object {
        fun of(settings: SettingsEntry, registrationFilesCanBeDownloaded: Boolean) = RestStartDownloadRegistrationFiles(
            startDownloadRegistrationFiles = settings.startDownloadRegistrationFiles,
            registrationFilesCanBeDownloaded = registrationFilesCanBeDownloaded
        )
    }
}