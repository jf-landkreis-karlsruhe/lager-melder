package de.kordondev.attendee.rest.controller

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
                .map { RestAttendee.of(it) }
    }

    @GetMapping("/attendee/{id}")
    fun getAttendee(@PathVariable(value = "id") id: Long): RestAttendee {
        return attendeeService
                .getAttendee(id)
                .let { RestAttendee.of(it) };
    }

    @PostMapping("/attendee")
    fun addAttendee(@RequestBody(required = true) attendee: RestAttendeeRequest): RestAttendee {
        val department = departmentService.getDepartment(attendee.departmentId)
        return attendeeService
                .createAttendee(RestAttendeeRequest.to(attendee, department))
                .let { RestAttendee.of(it)}
    }

    @PutMapping("/attendee/{id}")
    fun saveAttendee(@RequestBody(required = true) attendee: RestAttendeeRequest, @PathVariable("id") id: Long) {
        val department = departmentService.getDepartment(attendee.departmentId)
        return attendeeService
                .saveAttendee(id, RestAttendeeRequest.to(attendee, department))
                .let { RestAttendee.of(it)}
    }

    @DeleteMapping("/attendee/{id}")
    fun deleteAttendee(@PathVariable(value = "id") id: Long) {
        attendeeService.deleteAttendee(id)
    }
}