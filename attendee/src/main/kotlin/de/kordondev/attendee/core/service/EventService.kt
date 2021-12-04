package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.model.AttendeeInEvent
import de.kordondev.attendee.core.model.Event
import de.kordondev.attendee.core.model.NewAttendeeCodeInEventCode
import de.kordondev.attendee.core.persistence.EventRepository
import de.kordondev.attendee.core.persistence.entry.AttendeeInEventEntry
import de.kordondev.attendee.core.persistence.entry.EventEntry
import de.kordondev.attendee.core.persistence.repository.AttendeeInEventRepository
import de.kordondev.attendee.exception.NotFoundException
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class EventService(
    val attendeeService: AttendeeService,
    val eventRepository: EventRepository,
    val attendeeInEventRepository: AttendeeInEventRepository
) {
    fun getEventByCode(code: String): Event {
        return eventRepository.findByCodeOrNull(code)
            ?.let { EventEntry.to(it) }
            ?: throw NotFoundException("Event not found for code $code")
    }

    fun addAttendeeToEvent(eventCode: String, attendeeCode: String): AttendeeInEvent {
        val event: Event = getEventByCode(eventCode)
        val attendee: Attendee = attendeeService.getAttendeeByCode(attendeeCode)
        val attendeeInEvent = NewAttendeeCodeInEventCode(attendeeCode, eventCode, Instant.now())
        return attendeeInEventRepository.save(AttendeeInEventEntry.of(attendeeInEvent))
            .let { AttendeeInEvent(attendee.firstName, attendee.lastName, event.name, it.time) }
    }
}