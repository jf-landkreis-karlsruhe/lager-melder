package de.kordondev.lagermelder.rest.model.request

import de.kordondev.lagermelder.core.model.Settings
import java.time.Instant

data class RestStartDownloadRegistrationFiles(
    val startDownloadRegistrationFiles: Instant,
    val registrationFilesCanBeDownloaded: Boolean
) {
    companion object {
        fun of(settings: Settings, registrationFilesCanBeDownloaded: Boolean) = RestStartDownloadRegistrationFiles(
            startDownloadRegistrationFiles = settings.startDownloadRegistrationFiles,
            registrationFilesCanBeDownloaded = registrationFilesCanBeDownloaded
        )
    }
}