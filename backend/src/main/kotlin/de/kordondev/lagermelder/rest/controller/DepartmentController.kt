package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.service.AttendeeService
import de.kordondev.lagermelder.core.service.DepartmentService
import de.kordondev.lagermelder.core.service.TentsService
import de.kordondev.lagermelder.rest.model.RestAttendee
import de.kordondev.lagermelder.rest.model.RestDepartment
import de.kordondev.lagermelder.rest.model.RestTents
import de.kordondev.lagermelder.rest.model.request.RestDepartmentRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
class DepartmentController(
    private val departmentService: DepartmentService,
    private val attendeeService: AttendeeService,
    private val tentsService: TentsService
) {
    @GetMapping("/departments")
    fun getDepartments(@RequestParam(defaultValue = "false") onlyWithAttendees: Boolean): List<RestDepartment> {
        return departmentService
            .getDepartments(onlyWithAttendees)
                .map{ RestDepartment.of(it)}
    }

    @GetMapping("/departments/{id}")
    fun getDepartment(@PathVariable(value = "id") id: Long): RestDepartment {
        return departmentService
                .getDepartment(id)
                .let{ RestDepartment.of(it)}
    }

    @PostMapping("/departments")
    fun addDepartment(@RequestBody(required = true) @Valid department: RestDepartmentRequest): RestDepartment {
        return departmentService
                .createDepartment(RestDepartmentRequest.to(department))
                .let { RestDepartment.of(it) }
    }

    @PutMapping("/departments/{id}")
    fun saveDepartment(@RequestBody(required = true) @Valid department: RestDepartmentRequest, @PathVariable("id") id: Long): RestDepartment {

        return departmentService
            .saveDepartment(RestDepartmentRequest.to(department, id))
                .let { RestDepartment.of(it) }
    }

    @DeleteMapping("/departments/{id}")
    fun deleteDepartment(@PathVariable("id") id: Long) {
        departmentService.deleteDepartment(id)
    }

    @GetMapping("/departments/{id}/attendees")
    fun getAttendeesForDepartment(@PathVariable(value = "id") id: Long): Iterable<RestAttendee> {
        return departmentService
                .getDepartment(id)
                .let { attendeeService.getAttendeesForDepartment(it) }
                .let { attendees -> attendees.map { attendee -> RestAttendee.of(attendee) } }
    }

    @GetMapping("departments/{id}/tents")
    fun getForDepartment(
        @PathVariable(required = true)id: Long,
    ): RestTents {
        return departmentService
            .getDepartment(id)
            .let(tentsService::getForDepartment)
            .let { RestTents.of(it) }
    }
}