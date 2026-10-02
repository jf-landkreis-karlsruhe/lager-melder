package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.service.SettingsService
import de.kordondev.lagermelder.rest.model.RestSettings
import de.kordondev.lagermelder.rest.model.request.RestRegistrationEnd
import de.kordondev.lagermelder.rest.model.request.RestSettingsRequest
import de.kordondev.lagermelder.rest.model.request.RestStartDownloadRegistrationFiles
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SettingsController(
    private val settingsService: SettingsService,
) {
    @GetMapping("/settings")
    fun getSettings(): RestSettings =
        settingsService
            .getSettings()
            .let { RestSettings.of(it) }

    @PutMapping("/settings")
    fun updateSettings(
        @RequestBody(required = true) @Valid settings: RestSettingsRequest,
    ): RestSettings =
        settingsService
            .saveSettings(RestSettingsRequest.to(settings))
            .let { RestSettings.of(it) }

    @GetMapping("/settings/registration-end")
    fun getRegistrationEnd(): RestRegistrationEnd =
        settingsService
            .getSettings()
            .let {
                RestRegistrationEnd.of(
                    it,
                    settingsService.attendeesCanBeEdited(),
                    settingsService.childGroupsCanBeEdited(),
                    settingsService.helpersCanBeEdited(),
                )
            }

    @GetMapping("/settings/start-download-registration-files")
    fun getStartDownloadRegistrationFiles(): RestStartDownloadRegistrationFiles =
        settingsService
            .getSettings()
            .let { RestStartDownloadRegistrationFiles.of(it, settingsService.canRegistrationFilesDownloaded()) }
}
