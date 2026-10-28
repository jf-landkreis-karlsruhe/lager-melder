package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.persistence.entry.*
import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
import de.kordondev.lagermelder.core.persistence.repository.SettingsRepository
import de.kordondev.lagermelder.core.security.AuthorityService
import de.kordondev.lagermelder.exception.BadRequestException
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Service
class SettingsService(
    private val settingsRepository: SettingsRepository,
    private val authorityService: AuthorityService,
) {
    companion object {
        private const val SETTINGS_ID = 1L
    }

    fun getSettings(): SettingsEntry {
        val settingsList = settingsRepository.findAll().toList()
        if (settingsList.isEmpty()) {
            return saveSettings(
                SettingsEntry(
                    id = SETTINGS_ID,
                    registrationEnd = LocalDate.now().plusDays(30),
                    startDownloadRegistrationFiles = LocalDate.now().plusDays(45),
                    hostCity = "Austragungsort",
                    eventStart = LocalDate.now().plusDays(60),
                    eventEnd = LocalDate.now().plusDays(65),
                    eventName = "",
                    eventAddress = "",
                    organizer = "",
                    organisationAddress = "",
                    moneyPerYouthLoader = "8,99",
                    childGroupsRegistrationEnd = LocalDate.now().plusDays(35),
                    helpersRegistrationEnd = LocalDate.now().plusDays(33),
                    numberOfDuties = 0,
                ),
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

    fun canBeEdited(attendee: Attendee): Boolean =
        when (attendee) {
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

            else -> {
                false
            }
        }

    fun attendeesCanBeEdited(): Boolean {
        if (authorityService.isSpecializedFieldDirectorFilter()) {
            return true
        }
        return isOnOrBefore(getSettings().registrationEnd)
    }

    fun childGroupsCanBeEdited(): Boolean {
        if (authorityService.isSpecializedFieldDirectorFilter()) {
            return true
        }
        return isOnOrBefore(getSettings().childGroupsRegistrationEnd)
    }

    fun canRegistrationFilesDownloaded(): Boolean = !SettingsEntry.today().isBefore(getSettings().startDownloadRegistrationFiles)

    fun helpersCanBeEdited(): Boolean {
        if (authorityService.isSpecializedFieldDirectorFilter()) {
            return true
        }
        return isOnOrBefore(getSettings().helpersRegistrationEnd)
    }

    /** The last day of a registration period is included until 23:59 (Europe/Berlin). */
    private fun isOnOrBefore(lastDay: LocalDate): Boolean = !SettingsEntry.today().isAfter(lastDay)

    fun canCheckInAttendees(): Boolean {
        // Check if it is at most one week before the event
        return LocalDate.now().isAfter(getSettings().eventStart.minus(7, ChronoUnit.DAYS))
    }
}
