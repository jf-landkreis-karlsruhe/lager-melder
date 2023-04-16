package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.model.Settings
import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.core.persistence.entry.SettingsEntry
import de.kordondev.attendee.core.persistence.repository.SettingsRepository
import de.kordondev.attendee.core.security.AuthorityService
import de.kordondev.attendee.exception.BadRequestException
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
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
                Settings(
                    id = SETTINGS_ID,
                    registrationEnd = Instant.now().plus(30, ChronoUnit.DAYS),
                    startDownloadRegistrationFiles = Instant.now().plus(45, ChronoUnit.DAYS),
                    hostCity = "Austragungsort",
                    eventStart = LocalDate.now().plusDays(60),
                    eventEnd = LocalDate.now().plusDays(65),
                    eventName = "",
                    eventAddress = "",
                    organizer = "",
                    organisationAddress = "",
                    moneyPerYouthLoader = "8,99",
                )
            )
        }
        return settingsList.first()
    }

    fun saveSettings(settings: Settings): Settings {
        authorityService.hasRole(listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        if (settings.registrationEnd.isBefore(settings.startDownloadRegistrationFiles)) {
            throw BadRequestException("Registrierungsende muss vor dem Start der Downloads der Anmeldeunterlagen sein.")
        }
        return settingsRepository.save(SettingsEntry.of(settings, SETTINGS_ID))
            .let { SettingsEntry.to(it) }
    }


    fun canAttendeesBeEdited(): Boolean {
        if (authorityService.isSpecializedFieldDirectorFilter()) {
            return true
        }
        val endRegistration = getSettings().registrationEnd
        return Instant.now().isBefore(endRegistration)
    }

    fun canRegistrationFilesDownloaded(): Boolean {
        return Instant.now().isAfter(getSettings().startDownloadRegistrationFiles)
    }
}