package de.kordondev.attendee.rest.model.request

import de.kordondev.attendee.core.model.Settings
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