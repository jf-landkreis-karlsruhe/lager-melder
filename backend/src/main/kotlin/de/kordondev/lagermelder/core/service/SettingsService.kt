package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.persistence.entry.SettingsEntry
import de.kordondev.lagermelder.core.persistence.repository.SettingsRepository
import de.kordondev.lagermelder.core.security.AuthorityService
import de.kordondev.lagermelder.exception.BadRequestException
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

    fun getSettings(): SettingsEntry {
        val settingsList = settingsRepository.findAll().toList()
        if (settingsList.isEmpty()) {
            return saveSettings(
                SettingsEntry(
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

    fun saveSettings(settings: SettingsEntry): SettingsEntry {
        authorityService.hasRole(listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        if (settings.registrationEnd.isAfter(settings.startDownloadRegistrationFiles)) {
            throw BadRequestException("Registrierungsende muss vor dem Start der Downloads der Anmeldeunterlagen sein.")
        }
        return settingsRepository.save(settings.copy(id = SETTINGS_ID))
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