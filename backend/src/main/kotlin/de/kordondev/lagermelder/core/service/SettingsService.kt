package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.persistence.entry.*
import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
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
                    childGroupsRegistrationEnd = Instant.now().plus(35, ChronoUnit.DAYS),
                    helpersRegistrationEnd = Instant.now().plus(33, ChronoUnit.DAYS),
                    numberOfDuties = 0
                )
            )
        }
        return settingsList.first()
    }

    fun saveSettings(settings: SettingsEntry): SettingsEntry {
        authorityService.isSpecializedFieldDirector()
        if (settings.registrationEnd.isAfter(settings.startDownloadRegistrationFiles)) {
            throw BadRequestException("Registrierungsende muss vor dem Start der Downloads der Anmeldeunterlagen sein.")
        }
        return settingsRepository.save(settings.copy(id = SETTINGS_ID))
    }


    fun canBeEdited(attendee: Attendee): Boolean {
        return when (attendee) {
            is YouthEntry, is YouthLeaderEntry -> {
                attendeesCanBeEdited()
            }

            is ChildEntry, is ChildLeaderEntry -> {
                childGroupsCanBeEdited()
            }

            is HelperEntity -> {
                helpersCanBeEdited()
            }

            is ZKidEntry -> {
                attendeesCanBeEdited()
            }

            else -> false
        }
    }

    fun attendeesCanBeEdited(): Boolean {
        if (authorityService.isSpecializedFieldDirectorFilter()) {
            return true
        }
        return Instant.now().isBefore(getSettings().registrationEnd)
    }

    fun childGroupsCanBeEdited(): Boolean {
        if (authorityService.isSpecializedFieldDirectorFilter()) {
            return true
        }
        return Instant.now().isBefore(getSettings().childGroupsRegistrationEnd)
    }

    fun canRegistrationFilesDownloaded(): Boolean {
        return Instant.now().isAfter(getSettings().startDownloadRegistrationFiles)
    }

    fun helpersCanBeEdited(): Boolean {
        if (authorityService.isSpecializedFieldDirectorFilter()) {
            return true
        }
        return Instant.now().isBefore(getSettings().helpersRegistrationEnd)
    }

    fun canCheckInAttendees(): Boolean {
        // Check if it is at most one week before the event
        return LocalDate.now().isAfter(getSettings().eventStart.minus(7, ChronoUnit.DAYS))
    }
}