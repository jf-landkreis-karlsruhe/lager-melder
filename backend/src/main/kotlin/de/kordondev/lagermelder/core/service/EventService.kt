package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.persistence.entry.*
import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
import de.kordondev.lagermelder.core.persistence.repository.AttendeeInEventRepository
import de.kordondev.lagermelder.core.persistence.repository.EventRepository
import de.kordondev.lagermelder.core.security.AuthorityService
import de.kordondev.lagermelder.core.security.PasswordGenerator
import de.kordondev.lagermelder.core.service.helper.AttendeeInEvent
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
        val attendee: Attendee = attendeeService.getAttendeeByCode(attendeeCode)
        val attendeeInEvent = AttendeeInEventEntry(id = 0, attendeeCode, eventCode, Instant.now())
        var attendeeStatus: AttendeeStatus? = null
        if (event.type == EventType.GlobalEnter) {
            attendeeStatus = AttendeeStatus.ENTERED
        }
        if (event.type == EventType.GlobalLeave) {
            attendeeStatus = AttendeeStatus.LEFT
        }
        if (attendeeStatus != null) {
            attendeeService.updateAttendeeStatus(attendee, attendeeStatus)
        }
        return attendeeInEventRepository.save(attendeeInEvent)
            .let {
                AttendeeInEvent(
                    attendeeFirstName = attendee.firstName,
                    attendeeLastName = attendee.lastName,
                    eventName = event.name,
                    time = attendeeInEvent.time
                )
            }
    }

    fun getGlobalEventSummary(): RestGlobalEventSummary {
        val attendees = attendeeService.getAllAttendees()
        val allAttendees = (attendees.youths + attendees.youthLeaders + attendees.childLeaders + attendees.children + attendees.zKids)
        val attendeesByDepartment = allAttendees.groupBy { attendeeService.getPartOfDepartmentOrDepartment(it) }

        val notPausedAttendees = attendeesByDepartment.entries
            .filter { !it.key.paused }
            .flatMap { it.value }

        return RestGlobalEventSummary(
            total = sumUp(notPausedAttendees, "Zeltlager Gesamt", false),
            departments = attendeesByDepartment.keys.map { sumUp(attendeesByDepartment[it]!!, it.name, it.paused) }
        )
    }

    fun addAttendeesToEvent(eventCode: String, attendeeCodes: List<String>): List<AttendeeInEvent> {
        return attendeeCodes.map { addAttendeeToEvent(eventCode, it) }
    }

    private fun sumUp(attendees: List<Attendee>, name: String, paused: Boolean): Distribution {
        val groupedAttendees = attendees.groupBy { attendeeRoleStatus(it.status, it.role) }
        return Distribution(
            name = name,
            paused = paused,
            youths = groupedAttendees[attendeeRoleStatus(
                AttendeeStatus.ENTERED,
                AttendeeRole.YOUTH
            )]?.size ?: 0,
            youthLeaders = groupedAttendees[attendeeRoleStatus(
                AttendeeStatus.ENTERED,
                AttendeeRole.YOUTH_LEADER
            )]?.size ?: 0,
            zKids = groupedAttendees[attendeeRoleStatus(
                AttendeeStatus.ENTERED,
                AttendeeRole.Z_KID
            )]?.size ?: 0,
            children = groupedAttendees[attendeeRoleStatus(
                AttendeeStatus.ENTERED,
                AttendeeRole.CHILD
            )]?.size ?: 0,
            childLeaders = groupedAttendees[attendeeRoleStatus(
                AttendeeStatus.ENTERED,
                AttendeeRole.CHILD_LEADER
            )]?.size ?: 0,
        )
    }

    private fun attendeeRoleStatus(status: AttendeeStatus?, role: AttendeeRole): String {
        return "$status + $role"
    }

    fun addDepartmentToEvent(eventCode: String, departmentId: Long): List<AttendeeInEvent> {
        return departmentService.getDepartment(departmentId)
            .let { attendeeService.getAttendeesForDepartment(it) }
            .let { it.youths + it.youthLeaders }
            .map { addAttendeeToEvent(eventCode, it.code) }
    }
}