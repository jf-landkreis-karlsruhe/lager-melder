package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.model.Settings
import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.core.persistence.entry.SettingsEntry
import de.kordondev.attendee.core.persistence.repository.SettingsRepository
import de.kordondev.attendee.core.security.AuthorityService
import de.kordondev.attendee.exception.EndOfRegistrationExceededException
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class SettingsService(
    private val settingsRepository: SettingsRepository,
    private val authorityService: AuthorityService
) {

    private val SETTINGS_ID = 1L

    fun getSettings(): Settings {
        val settingsList = settingsRepository.findAll()
            .map { SettingsEntry.to(it) }
        if (settingsList.isEmpty()) {
            return saveSettings(
                Settings(id = 1, registrationEnd = Instant.now().plus(1L, ChronoUnit.DAYS))
            )
        }
        return settingsList.first()
    }

    fun saveSettings(settings: Settings): Settings {
        authorityService.hasRole(listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        return settingsRepository.save(SettingsEntry.of(settings, SETTINGS_ID))
            .let { SettingsEntry.to(it) }
    }

    fun canAttendeesBeEdited() {
        val endRegistration = getSettings().registrationEnd
        if (endRegistration.isBefore(Instant.now())) {
            throw EndOfRegistrationExceededException("Registrierungsende wurde überschritten")
        }
    }
}