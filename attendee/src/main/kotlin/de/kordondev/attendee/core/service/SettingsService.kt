package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.model.Settings
import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.core.persistence.entry.SettingsEntry
import de.kordondev.attendee.core.persistence.repository.SettingsRepository
import de.kordondev.attendee.core.security.AuthorityService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class SettingsService(
    private val settingsRepository: SettingsRepository,
    private val authorityService: AuthorityService
) {

    fun getSettings(): Settings {
        val settingsList = settingsRepository.findAll()
            .map { SettingsEntry.to(it) }
        if (settingsList.isEmpty()) {
            return saveSettings(
                Settings(id = 1, registrationEnd = LocalDateTime.MAX)
            )
        }
        return settingsList.first()
    }

    fun saveSettings(settings: Settings): Settings {
        authorityService.hasRole(listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        return getSettings()
            .let { SettingsEntry.of(settings, it.id) }
            .let { settingsRepository.save(it) }
            .let { SettingsEntry.to(it) }
    }
}