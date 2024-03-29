package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.model.AttendeeInEvent
import de.kordondev.lagermelder.core.model.NewAttendeeCodeInEventCode
import de.kordondev.lagermelder.core.persistence.entry.*
import de.kordondev.lagermelder.core.persistence.repository.AttendeeInEventRepository
import de.kordondev.lagermelder.core.persistence.repository.AttendeeRepository
import de.kordondev.lagermelder.core.persistence.repository.EventRepository
import de.kordondev.lagermelder.core.security.AuthorityService
import de.kordondev.lagermelder.core.security.PasswordGenerator
import de.kordondev.lagermelder.exception.NotDeletableException
import de.kordondev.lagermelder.exception.NotFoundException
import de.kordondev.lagermelder.rest.model.Distribution
import de.kordondev.lagermelder.rest.model.RestGlobalEventSummary
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class EventService(
    val attendeeService: AttendeeService,
    val authorityService: AuthorityService,
    val eventRepository: EventRepository,
    val attendeeInEventRepository: AttendeeInEventRepository,
    val attendeeRepository: AttendeeRepository,
    val departmentService: DepartmentService
) {
    fun getEventByCode(code: String): EventEntry {
        return eventRepository.findByCodeAndTrashedIsFalse(code)
            ?: throw NotFoundException("Event not found for code $code")
    }

    fun getEventByType(type: EventType): EventEntry {
        return eventRepository.findByTypeAndTrashedIsFalse(type)
            ?: throw NotFoundException("Event not found for type $type")
    }

    fun getEvent(id: Long): EventEntry {
        return eventRepository.findByIdAndTrashedIsFalse(id)
            ?: throw NotFoundException("Event with $id not found")
    }

    fun getEvents(): List<EventEntry> {
        return eventRepository.findAllByTrashedIsFalse()
    }

    fun createEvent(event: EventEntry): EventEntry {
        authorityService.hasRole(listOf(Roles.USER, Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        val code = PasswordGenerator.generateCode()
        return eventRepository.save(event.copy(code = code))
    }

    fun saveEvent(id: Long, event: EventEntry): EventEntry {
        authorityService.hasRole(listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        return eventRepository.findByIdAndTrashedIsFalse(id)
            ?.let {
                eventRepository.save(event.copy(code = it.code, id = id, type = it.type))
            }
            ?: createEvent(event)
    }

    fun deleteEvent(id: Long) {
        authorityService.hasRole(listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        getEvent(id).let {
            if (it.type != EventType.Location) {
                throw NotDeletableException("Das Event ${it.name} kann nicht gelöscht werden")
            }
            eventRepository.save(it.copy(trashed = true))
        }
    }


    fun addAttendeeToEvent(eventCode: String, attendeeCode: String): AttendeeInEvent {
        val event = getEventByCode(eventCode)
        val attendee: AttendeeEntry = attendeeService.getAttendeeByCode(attendeeCode)
        val attendeeInEvent = NewAttendeeCodeInEventCode(attendeeCode, eventCode, Instant.now())
        var attendeeStatus: AttendeeStatus? = null
        if (event.type == EventType.GlobalEnter) {
            attendeeStatus = AttendeeStatus.ENTERED
        }
        if (event.type == EventType.GlobalLeave) {
            attendeeStatus = AttendeeStatus.LEFT
        }
        attendeeRepository.save(attendee.copy(status = attendeeStatus))
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

    private fun sumUp(attendees: List<AttendeeEntry>, name: String): Distribution {
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

    fun addDepartmentToEvent(eventCode: String, departmentId: Long): List<AttendeeInEvent> {
        return departmentService.getDepartment(departmentId)
            .let { attendeeService.getAttendeesForDepartment(it) }
            .map { addAttendeeToEvent(eventCode, it.code) }
    }
}