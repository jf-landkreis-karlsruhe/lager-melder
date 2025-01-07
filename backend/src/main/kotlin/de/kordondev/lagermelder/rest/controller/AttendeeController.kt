package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.persistence.entry.AttendeeRole
import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.entry.EventDayEntity
import de.kordondev.lagermelder.core.service.AttendeeService
import de.kordondev.lagermelder.core.service.DepartmentService
import de.kordondev.lagermelder.core.service.EventDayService
import de.kordondev.lagermelder.rest.model.RestAttendee
import de.kordondev.lagermelder.rest.model.RestAttendees
import de.kordondev.lagermelder.rest.model.request.RestAttendeeRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
class AttendeeController(
    private val attendeeService: AttendeeService,
    private val departmentService: DepartmentService,
    private val eventDayService: EventDayService
) {
    @GetMapping("/attendees")
    fun getAttendees(): RestAttendees {
        return attendeeService
            .getAttendees()
            .let { RestAttendees.of(it) }
    }

    @GetMapping("/attendees/{id}")
    fun getAttendee(@PathVariable(value = "id") id: String): RestAttendee {
        return attendeeService
            .getAttendee(id)
            .let { RestAttendee.of(it) }
    }

    @PostMapping("/attendees")
    fun addAttendee(@RequestBody(required = true) @Valid attendee: RestAttendeeRequest): RestAttendee {
        val department = departmentService.getDepartment(attendee.departmentId)
        return attendeeService
            .createAttendee(
                RestAttendeeRequest.to(
                    attendee,
                    department,
                    getPartOfDepartmentForZKid(attendee),
                    getEventDays(attendee)
                )
            ).let { RestAttendee.of(it) }
    }

    @PutMapping("/attendees/{id}")
    fun saveAttendee(
        @RequestBody(required = true) @Valid attendee: RestAttendeeRequest,
        @PathVariable("id") id: String
    ): RestAttendee {
        val department = departmentService.getDepartment(attendee.departmentId)
        return attendeeService
                .saveAttendee(id, RestAttendeeRequest.to(attendee, department, getPartOfDepartmentForZKid(attendee), getEventDays(attendee)))
                .let { RestAttendee.of(it)}
    }

    @DeleteMapping("/attendees/{id}")
    fun deleteAttendee(@PathVariable(value = "id") id: String) {
        attendeeService.deleteAttendee(id)
    }

    private fun getPartOfDepartmentForZKid(attendee: RestAttendeeRequest): DepartmentEntry? {
        if (attendee.role == AttendeeRole.Z_KID) {
            return departmentService.getDepartment(attendee.partOfDepartmentId)
        }
        return null
    }

    private fun getEventDays(attendee: RestAttendeeRequest): Set<EventDayEntity> {
        if (attendee.role === AttendeeRole.HELPER) {
            return eventDayService.getEventDays().toSet()
        }
        return emptySet()
    }
}