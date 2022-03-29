package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.service.SettingsService
import de.kordondev.attendee.rest.model.RestSettings
import de.kordondev.attendee.rest.model.request.RestRegistrationEnd
import de.kordondev.attendee.rest.model.request.RestSettingsRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class SettingsController(
    private val settingsService: SettingsService
) {

    @GetMapping("/settings")
    fun getSettings(): RestSettings {
        return settingsService.getSettings()
            .let { RestSettings.of(it) }
    }

    @PutMapping("/settings")
    fun updateSettings(@RequestBody(required = true) @Valid settings: RestSettingsRequest): RestSettings {
        return settingsService.saveSettings(RestSettingsRequest.to(settings))
            .let { RestSettings.of(it) }
    }

    @GetMapping("/settings/registration-end")
    fun getRegistrationEnd(): RestRegistrationEnd {
        return settingsService.getSettings()
            .let { RestRegistrationEnd.of(it, settingsService.canAttendeesBeEdited()) }
    }
}