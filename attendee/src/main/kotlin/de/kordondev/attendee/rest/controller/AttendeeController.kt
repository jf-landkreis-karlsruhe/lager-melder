package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.model.NewAttendee
import de.kordondev.attendee.core.service.AttendeeService
import de.kordondev.attendee.core.service.DepartmentService
import de.kordondev.attendee.rest.model.RestAttendee
import de.kordondev.attendee.rest.model.request.RestAttendeeRequest
import org.springframework.web.bind.annotation.*

@RestController
class AttendeeController(
        private val attendeeService: AttendeeService,
        private val departmentService: DepartmentService
) {
    @GetMapping("/attendee")
    fun getAttendees(): List<RestAttendee> {
        return attendeeService
                .getAttendees()
                .map { attendee -> RestAttendee.of(attendee) }
    }

    @GetMapping("/attendee/{id}")
    fun getAttendee(@PathVariable(value = "id") id: Long): RestAttendee {
        return attendeeService
                .getAttendee(id)
                .let { attendee -> RestAttendee.of(attendee) };
    }

    @PostMapping("/attendee")
    fun saveAttendee(@RequestBody(required = true) attendee: RestAttendeeRequest): RestAttendee {
        val department = departmentService.getDepartment(attendee.departmentId)
        return attendeeService
                .saveAttendee(NewAttendee(
                        firstName = attendee.firstName,
                        lastName = attendee.lastName,
                        department = department
                ))
                .let { savedAttendee -> RestAttendee.of(savedAttendee)}
    }

    @DeleteMapping("/attendee/{id}")
    fun deleteAttendee(@PathVariable(value = "id") id: Long) {
        attendeeService.deleteAttendee(id)
    }
}