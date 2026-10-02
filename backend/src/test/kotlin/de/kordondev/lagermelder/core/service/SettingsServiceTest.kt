package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.persistence.entry.AttendeeRole
import de.kordondev.lagermelder.core.persistence.entry.ChildEntry
import de.kordondev.lagermelder.core.persistence.entry.Food
import de.kordondev.lagermelder.core.persistence.entry.HelperEntity
import de.kordondev.lagermelder.core.persistence.entry.SettingsEntry
import de.kordondev.lagermelder.core.persistence.repository.SettingsRepository
import de.kordondev.lagermelder.core.security.AuthorityService
import de.kordondev.lagermelder.exception.BadRequestException
import de.kordondev.lagermelder.helper.Entities
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class SettingsServiceTest {
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var authorityService: AuthorityService
    private lateinit var settingsService: SettingsService

    @BeforeEach
    fun setUp() {
        settingsRepository = mock()
        authorityService = mock()
        settingsService = SettingsService(settingsRepository, authorityService)
    }

    @Test
    fun youthCanBeEditedBeforeRegistrationEnd() {
        givenSettings(Entities.settings().copy(registrationEnd = inDays(1)))

        assertThat(settingsService.canBeEdited(Entities.attendee())).isTrue()
    }

    @Test
    fun youthCannotBeEditedAfterRegistrationEnd() {
        givenSettings(Entities.settings().copy(registrationEnd = inDays(-1)))

        assertThat(settingsService.canBeEdited(Entities.attendee())).isFalse()
    }

    @Test
    fun specializedFieldDirectorCanEditAfterRegistrationEnd() {
        givenSettings(Entities.settings().copy(registrationEnd = inDays(-1)))
        whenever(authorityService.isSpecializedFieldDirectorFilter()).doReturn(true)

        assertThat(settingsService.canBeEdited(Entities.attendee())).isTrue()
    }

    @Test
    fun childrenUseChildGroupsRegistrationEnd() {
        givenSettings(Entities.settings().copy(registrationEnd = inDays(-1), childGroupsRegistrationEnd = inDays(1)))

        assertThat(settingsService.canBeEdited(child())).isTrue()
    }

    @Test
    fun helpersUseHelpersRegistrationEnd() {
        givenSettings(Entities.settings().copy(registrationEnd = inDays(1), helpersRegistrationEnd = inDays(-1)))

        assertThat(settingsService.canBeEdited(helper())).isFalse()
    }

    @Test
    fun checkInIsPossibleWithinOneWeekBeforeTheEvent() {
        givenSettings(Entities.settings().copy(eventStart = LocalDate.now().plusDays(6)))

        assertThat(settingsService.canCheckInAttendees()).isTrue()
    }

    @Test
    fun checkInIsNotPossibleEarlierThanOneWeekBeforeTheEvent() {
        givenSettings(Entities.settings().copy(eventStart = LocalDate.now().plusDays(8)))

        assertThat(settingsService.canCheckInAttendees()).isFalse()
    }

    @Test
    fun registrationFilesCanBeDownloadedAfterStartDate() {
        givenSettings(Entities.settings().copy(startDownloadRegistrationFiles = inDays(-1)))

        assertThat(settingsService.canRegistrationFilesDownloaded()).isTrue()
    }

    @Test
    fun saveSettingsRejectsRegistrationEndAfterDownloadStart() {
        val settings = Entities.settings().copy(registrationEnd = inDays(10), startDownloadRegistrationFiles = inDays(5))

        assertThatThrownBy { settingsService.saveSettings(settings) }.isInstanceOf(BadRequestException::class.java)
        verify(settingsRepository, never()).save(any())
    }

    @Test
    fun saveSettingsAlwaysUsesSingletonId() {
        whenever(settingsRepository.save(any<SettingsEntry>())).doAnswer { it.arguments[0] as SettingsEntry }

        val saved = settingsService.saveSettings(Entities.settings().copy(id = 42))

        assertThat(saved.id).isEqualTo(1L)
    }

    @Test
    fun getSettingsCreatesDefaultsWhenNoneExist() {
        whenever(settingsRepository.findAll()).doReturn(emptyList())
        whenever(settingsRepository.save(any<SettingsEntry>())).doAnswer { it.arguments[0] as SettingsEntry }

        val settings = settingsService.getSettings()

        assertThat(settings.id).isEqualTo(1L)
        assertThat(settings.registrationEnd).isBefore(settings.startDownloadRegistrationFiles)
    }

    private fun givenSettings(settings: SettingsEntry) {
        whenever(settingsRepository.findAll()).doReturn(listOf(settings))
    }

    private fun inDays(days: Long): Instant = Instant.now().plus(days, ChronoUnit.DAYS)

    private fun child() =
        ChildEntry(
            firstName = "child",
            lastName = "last",
            birthday = "2018-01-01",
            food = Food.MEAT,
            tShirtSize = "S",
            additionalInformation = "",
            code = "child",
            role = AttendeeRole.CHILD,
            department = Entities.department(),
            status = null,
        )

    private fun helper() =
        HelperEntity(
            firstName = "helper",
            lastName = "last",
            food = Food.MEAT,
            tShirtSize = "S",
            additionalInformation = "",
            code = "helper",
            role = AttendeeRole.HELPER,
            department = Entities.department(),
            status = null,
        )
}
