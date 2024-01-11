package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.service.DepartmentService
import de.kordondev.attendee.core.service.TentsService
import de.kordondev.attendee.rest.model.RestTents
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController()
class TentsController(
    private val tentsService: TentsService,
    private val departmentService: DepartmentService
) {

    @GetMapping("/tents/")
    fun getAllTents( ): List<RestTents> {
        return tentsService.getAllTents()
            .map { RestTents.of(it) }
    }

    @PutMapping("/tents/department/{departmentId}")
    fun updateTents(
        @PathVariable(required = true) departmentId: Long,
        @RequestBody(required = true) @Valid tents: RestTents
    ): RestTents {
        val department = departmentService.getDepartment(tents.departmentId)
        return tentsService
            .saveForDepartment(RestTents.to(tents, department))
            .let { RestTents.of(it)}
    }

}