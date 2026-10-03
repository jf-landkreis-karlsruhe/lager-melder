package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.persistence.entry.AttendeeInEventEntry
import de.kordondev.lagermelder.core.persistence.entry.AttendeeStatus
import de.kordondev.lagermelder.core.persistence.entry.EventEntry
import de.kordondev.lagermelder.core.persistence.entry.EventType
import de.kordondev.lagermelder.core.persistence.repository.AttendeeInEventRepository
import de.kordondev.lagermelder.core.persistence.repository.EventRepository
import de.kordondev.lagermelder.core.security.AuthorityService
import de.kordondev.lagermelder.exception.NotDeletableException
import de.kordondev.lagermelder.exception.WrongTimeException
import de.kordondev.lagermelder.helper.Entities
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class EventServiceTest {
    private lateinit var attendeeService: AttendeeService
    private lateinit var eventRepository: EventRepository
    private lateinit var attendeeInEventRepository: AttendeeInEventRepository
    private lateinit var settingsService: SettingsService
    private lateinit var eventService: EventService

    private val attendee = Entities.attendee()

    @BeforeEach
    fun setUp() {
        attendeeService = mock()
        eventRepository = mock()
        attendeeInEventRepository = mock()
        settingsService = mock()
        eventService =
            EventService(attendeeService, mock<AuthorityService>(), eventRepository, attendeeInEventRepository, mock(), settingsService)

        whenever(settingsService.canCheckInAttendees()).doReturn(true)
        whenever(attendeeService.getAttendeeByCode(attendee.code)).doReturn(attendee)
        whenever(attendeeInEventRepository.save(any<AttendeeInEventEntry>())).doAnswer { it.arguments[0] as AttendeeInEventEntry }
    }

    @ParameterizedTest
    @CsvSource("GlobalEnter, ENTERED", "GlobalLeave, LEFT")
    fun globalEventsUpdateAttendeeStatus(
        type: EventType,
        expectedStatus: AttendeeStatus,
    ) {
        givenEvent(Entities.eventEntry(type))

        eventService.addAttendeeToEvent("eventCode", attendee.code)

        verify(attendeeService).updateAttendeeStatus(attendee, expectedStatus)
    }

    @Test
    fun locationEventDoesNotChangeAttendeeStatus() {
        givenEvent(Entities.eventEntry(EventType.Location))

        eventService.addAttendeeToEvent("eventCode", attendee.code)

        verify(attendeeService, never()).updateAttendeeStatus(any(), any())
    }

    @Test
    fun addAttendeeToEventStoresCheckIn() {
        givenEvent(Entities.eventEntry(EventType.Location))

        val result = eventService.addAttendeeToEvent("eventCode", attendee.code)

        val captor = argumentCaptor<AttendeeInEventEntry>()
        verify(attendeeInEventRepository).save(captor.capture())
        assertThat(captor.firstValue.attendeeCode).isEqualTo(attendee.code)
        assertThat(captor.firstValue.eventCode).isEqualTo("eventCode")
        assertThat(result.attendeeFirstName).isEqualTo(attendee.firstName)
        assertThat(result.eventName).isEqualTo("event")
    }

    @Test
    fun addAttendeeToEventFailsOutsideCheckInWindow() {
        whenever(settingsService.canCheckInAttendees()).doReturn(false)

        assertThatThrownBy { eventService.addAttendeeToEvent("eventCode", attendee.code) }
            .isInstanceOf(WrongTimeException::class.java)
        verify(attendeeInEventRepository, never()).save(any<AttendeeInEventEntry>())
    }

    @Test
    fun deleteEventMarksLocationEventAsTrashed() {
        whenever(eventRepository.findByIdAndTrashedIsFalse(1L)).doReturn(Entities.eventEntry(EventType.Location))

        eventService.deleteEvent(1L)

        val captor = argumentCaptor<EventEntry>()
        verify(eventRepository).save(captor.capture())
        assertThat(captor.firstValue.trashed).isTrue()
    }

    @ParameterizedTest
    @CsvSource("GlobalEnter", "GlobalLeave")
    fun globalEventsCannotBeDeleted(type: EventType) {
        whenever(eventRepository.findByIdAndTrashedIsFalse(1L)).doReturn(Entities.eventEntry(type))

        assertThatThrownBy { eventService.deleteEvent(1L) }.isInstanceOf(NotDeletableException::class.java)
    }

    @Test
    fun saveEventKeepsCodeAndType() {
        val stored = Entities.eventEntry(EventType.GlobalEnter).copy(code = "stored")
        whenever(eventRepository.findByIdAndTrashedIsFalse(1L)).doReturn(stored)
        whenever(eventRepository.save(any<EventEntry>())).doAnswer { it.arguments[0] as EventEntry }

        val saved = eventService.saveEvent(1L, stored.copy(name = "renamed", code = "new", type = EventType.Location))

        assertThat(saved.name).isEqualTo("renamed")
        assertThat(saved.code).isEqualTo("stored")
        assertThat(saved.type).isEqualTo(EventType.GlobalEnter)
    }

    private fun givenEvent(event: EventEntry) {
        whenever(eventRepository.findByCodeAndTrashedIsFalse("eventCode")).doReturn(event)
    }
}
