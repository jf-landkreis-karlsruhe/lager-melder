package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.service.AttendeeService
import de.kordondev.lagermelder.core.service.DepartmentService
import de.kordondev.lagermelder.rest.model.RestAttendee
import de.kordondev.lagermelder.rest.model.request.RestAttendeeRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
class AttendeeController(
        private val attendeeService: AttendeeService,
        private val departmentService: DepartmentService
) {
    @GetMapping("/attendees")
    fun getAttendees(): List<RestAttendee> {
        return attendeeService
                .getAttendees()
                .map { RestAttendee.of(it) }
    }

    @GetMapping("/attendees/{id}")
    fun getAttendee(@PathVariable(value = "id") id: String): RestAttendee {
        return attendeeService
                .getAttendee(id)
                .let { RestAttendee.of(it) };
    }

    @PostMapping("/attendees")
    fun addAttendee(@RequestBody(required = true) @Valid attendee: RestAttendeeRequest): RestAttendee {
        val department = departmentService.getDepartment(attendee.departmentId)
        return attendeeService
                .createAttendee(RestAttendeeRequest.to(attendee, department))
                .let { RestAttendee.of(it)}
    }

    @PutMapping("/attendees/{id}")
    fun saveAttendee(@RequestBody(required = true) @Valid attendee: RestAttendeeRequest, @PathVariable("id") id: Long): RestAttendee {
        val department = departmentService.getDepartment(attendee.departmentId)
        return attendeeService
                .saveAttendee(id, RestAttendeeRequest.to(attendee, department))
                .let { RestAttendee.of(it)}
    }

    @DeleteMapping("/attendees/{id}")
    fun deleteAttendee(@PathVariable(value = "id") id: Long) {
        attendeeService.deleteAttendee(id)
    }
}