package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.model.*
import de.kordondev.attendee.core.persistence.entry.*
import de.kordondev.attendee.core.persistence.repository.AttendeeInEventRepository
import de.kordondev.attendee.core.persistence.repository.AttendeeRepository
import de.kordondev.attendee.core.persistence.repository.EventRepository
import de.kordondev.attendee.core.security.AuthorityService
import de.kordondev.attendee.core.security.PasswordGenerator
import de.kordondev.attendee.exception.NotDeletableException
import de.kordondev.attendee.exception.NotFoundException
import de.kordondev.attendee.rest.model.Distribution
import de.kordondev.attendee.rest.model.RestGlobalEventSummary
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class EventService(
    val attendeeService: AttendeeService,
    val authorityService: AuthorityService,
    val eventRepository: EventRepository,
    val attendeeInEventRepository: AttendeeInEventRepository,
    val attendeeRepository: AttendeeRepository
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
                eventRepository.save(EventEntry.of(event, it.code, id, it.type))
            }
            ?.let { EventEntry.to(it) }
            ?: createEvent(event)
    }

    fun deleteEvent(id: Long) {
        authorityService.hasRole(listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        getEvent(id).let {
            if (it.type != EventType.Location) {
                throw NotDeletableException("Das Event ${it.name} kann nicht gelöscht werden")
            }
            val trashEvent = it.copy(trashed = true)
            eventRepository.save(EventEntry.of(trashEvent, trashEvent.code))
        }
    }


    fun addAttendeeToEvent(eventCode: String, attendeeCode: String): AttendeeInEvent {
        val event: Event = getEventByCode(eventCode)
        val attendee: Attendee = attendeeService.getAttendeeByCode(attendeeCode)
        val attendeeInEvent = NewAttendeeCodeInEventCode(attendeeCode, eventCode, Instant.now())
        if (event.type == EventType.GlobalEnter) {
            attendee.status = AttendeeStatus.ENTERED
        }
        if (event.type == EventType.GlobalLeave) {
            attendee.status = AttendeeStatus.LEFT
        }
        attendeeRepository.save(AttendeeEntry.of(attendee, attendee.status))
        return attendeeInEventRepository.save(AttendeeInEventEntry.of(attendeeInEvent))
            .let { AttendeeInEvent(attendee.firstName, attendee.lastName, event.name, it.time) }
    }

    fun getGlobalEventSummary(): RestGlobalEventSummary {
        val allAttendees = attendeeService.getAllAttendees()
        val attendeesByDepartment = allAttendees.groupBy { it.department }

        return RestGlobalEventSummary(
            total = sumUp(allAttendees, "Zeltlager"),
            departments = attendeesByDepartment.keys.map { sumUp(attendeesByDepartment[it]!!, it.name) }
        )
    }

    private fun sumUp(attendees: List<Attendee>, name: String): Distribution {
        val groupedAttendees = attendees.groupBy { attendeeRoleStatus(it.status, it.role) }
        return Distribution(
            name = name,
            checkedInYouth = groupedAttendees[attendeeRoleStatus(
                AttendeeStatus.ENTERED,
                AttendeeRole.YOUTH
            )]?.size ?: 0,
            checkedInYouthLeader = groupedAttendees[attendeeRoleStatus(
                AttendeeStatus.ENTERED,
                AttendeeRole.YOUTH_LEADER
            )]?.size ?: 0,
            checkedOutYouth = (groupedAttendees[attendeeRoleStatus(
                AttendeeStatus.LEFT,
                AttendeeRole.YOUTH
            )]?.size ?: 0) + (groupedAttendees[attendeeRoleStatus(
                null,
                AttendeeRole.YOUTH
            )]?.size ?: 0),
            checkedOutYouthLeader = (groupedAttendees[attendeeRoleStatus(
                AttendeeStatus.LEFT,
                AttendeeRole.YOUTH_LEADER
            )]?.size ?: 0) + (groupedAttendees[attendeeRoleStatus(
                null,
                AttendeeRole.YOUTH_LEADER
            )]?.size ?: 0),
        )
    }

    private fun attendeeRoleStatus(status: AttendeeStatus?, role: AttendeeRole): String {
        return "$status + $role"

    }
}