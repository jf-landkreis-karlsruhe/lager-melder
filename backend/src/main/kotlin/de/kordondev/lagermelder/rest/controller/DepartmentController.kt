package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.service.AttendeeService
import de.kordondev.lagermelder.core.service.DepartmentService
import de.kordondev.lagermelder.core.service.TentsService
import de.kordondev.lagermelder.rest.model.RestAttendees
import de.kordondev.lagermelder.rest.model.RestDepartment
import de.kordondev.lagermelder.rest.model.RestDepartmentShort
import de.kordondev.lagermelder.rest.model.RestTents
import de.kordondev.lagermelder.rest.model.request.RestDepartmentPauseRequest
import de.kordondev.lagermelder.rest.model.request.RestDepartmentRegistrationRequest
import de.kordondev.lagermelder.rest.model.request.RestDepartmentRequest
import de.kordondev.lagermelder.rest.model.request.RestDepartmentTentMarkingRequest
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import kotlin.random.Random

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

    @GetMapping("/departments/for-selecting")
    fun getDepartmentsForSelecting(): List<RestDepartmentShort> {
        return departmentService
            .getDepartmentsForSelecting()
            .map{ RestDepartmentShort.of(it)}
    }

    @GetMapping("/departments/{id}")
    fun getDepartment(@PathVariable(value = "id") id: Long): RestDepartment {
        return departmentService
                .getDepartment(id)
                .let{ RestDepartment.of(it)}
    }

    @PostMapping("/departments")
    fun addDepartment(@RequestBody(required = true) @Valid department: RestDepartmentRequest): RestDepartment {
        val departmentId = Random.nextLong()
        return departmentService
            .createDepartment(RestDepartmentRequest.to(department, departmentId, emptySet(), null, emptySet()))
                .let { RestDepartment.of(it) }
    }

    @PutMapping("/departments/{id}")
    fun saveDepartment(@RequestBody(required = true) @Valid department: RestDepartmentRequest, @PathVariable("id") id: Long): RestDepartment {
        val departmentFromDB = departmentService.getDepartment(id)

        return departmentService
            .saveDepartment(
                RestDepartmentRequest.to(
                    department,
                    id,
                    departmentFromDB.features,
                    departmentFromDB.evacuationGroup,
                    departmentFromDB.tentMarkings
                )
            )
                .let { RestDepartment.of(it) }
    }

    @DeleteMapping("/departments/{id}")
    fun deleteDepartment(@PathVariable("id") id: Long) {
        departmentService.deleteDepartment(id)
    }

    @GetMapping("/departments/{id}/attendees")
    fun getAttendeesForDepartment(@PathVariable(value = "id") id: Long): RestAttendees {
        return departmentService
                .getDepartment(id)
            .let { attendeeService.getAttendeesForDepartment(it) }
            .let { RestAttendees.of(it) }
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

    @PutMapping("departments/registration/{id}")
    fun registerDepartment(
        @PathVariable("id") id: Long,
        @RequestBody(required = true) @Valid registrationRequest: RestDepartmentRegistrationRequest
    ): RestTents {
        val department = departmentService.getDepartment(id)
        departmentService
            .saveDepartment(department.copy(phoneNumber = registrationRequest.departmentPhoneNumber))
        return tentsService
            .saveForDepartment(RestTents.to(registrationRequest.tents, department))
            .let { RestTents.of(it) }
    }

    @PutMapping("departments/{id}/change-pausing")
    fun changePausingDepartment(
        @PathVariable("id") id: Long,
        @RequestBody(required = true) @Valid pauseRequest: RestDepartmentPauseRequest
    ): RestDepartment {
        val department = departmentService.getDepartment(id)
        return departmentService
            .saveDepartment(department.copy(paused = pauseRequest.paused))
            .let { RestDepartment.of(it) }
    }

    @Transactional
    @PutMapping("departments/{id}/evacuation-groups/{evacuationGroupId}/tent-markings")
    fun updateTentMarkings(
        @PathVariable("id") id: Long,
        @PathVariable("evacuationGroupId") evacuationGroupId: String,
        @RequestBody(required = true) @Valid tentMarkings: Set<RestDepartmentTentMarkingRequest>
    ): RestDepartment {
        return departmentService
            .updateTentMarkings(id, tentMarkings, evacuationGroupId)
            .let { RestDepartment.of(it) }
    }

}