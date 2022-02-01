package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.model.*
import de.kordondev.attendee.core.persistence.entry.AttendeeInEventEntry
import de.kordondev.attendee.core.persistence.entry.EventEntry
import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.core.persistence.repository.AttendeeInEventRepository
import de.kordondev.attendee.core.persistence.repository.EventRepository
import de.kordondev.attendee.core.security.AuthorityService
import de.kordondev.attendee.core.security.PasswordGenerator
import de.kordondev.attendee.exception.NotFoundException
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class EventService(
    val attendeeService: AttendeeService,
    val authorityService: AuthorityService,
    val eventRepository: EventRepository,
    val attendeeInEventRepository: AttendeeInEventRepository
) {
    fun getEventByCode(code: String): Event {
        return eventRepository.findByCodeAndTrashedIsFalse(code)
            ?.let { EventEntry.to(it) }
            ?: throw NotFoundException("Event not found for code $code")
    }

    fun getEvent(id: Long): Event {
        return eventRepository.findByIdAndTrashedIsFalse(id)?.let { EventEntry.to(it) }
            ?: throw NotFoundException("Event with $id not found")
    }

    fun getEvents(): List<Event> {
        return eventRepository.findAllByTrashedIsFalse()
            .map { EventEntry.to(it) }
    }

    fun createEvent(event: NewEvent): Event {
        authorityService.hasRole(listOf(Roles.USER, Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        val code = PasswordGenerator.generateCode()
        return eventRepository.save(EventEntry.of(event, code))
            .let { EventEntry.to(it) }
    }

    fun saveEvent(id: Long, event: NewEvent): Event {
        authorityService.hasRole(listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        return eventRepository.findByIdAndTrashedIsFalse(id)
            ?.let {
                eventRepository.save(EventEntry.of(event, it.code, id))
            }
            ?.let { EventEntry.to(it) }
            ?: createEvent(event)
    }

    fun deleteEvent(id: Long) {
        authorityService.hasRole(listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        getEvent(id)?.let {
            val trashEvent = it.copy(trashed = true)
            eventRepository.save(EventEntry.of(trashEvent, trashEvent.code))
        }
    }


    fun addAttendeeToEvent(eventCode: String, attendeeCode: String): AttendeeInEvent {
        val event: Event = getEventByCode(eventCode)
        val attendee: Attendee = attendeeService.getAttendeeByCode(attendeeCode)
        val attendeeInEvent = NewAttendeeCodeInEventCode(attendeeCode, eventCode, Instant.now())
        return attendeeInEventRepository.save(AttendeeInEventEntry.of(attendeeInEvent))
            .let { AttendeeInEvent(attendee.firstName, attendee.lastName, event.name, it.time) }
    }
}