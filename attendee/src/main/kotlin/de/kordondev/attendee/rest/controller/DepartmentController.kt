package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.service.DepartmentService
import de.kordondev.attendee.rest.model.RestDepartment
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class DepartmentController(
        private val departmentService: DepartmentService
) {
    @GetMapping("/department")
    fun getDepartments(): List<RestDepartment> {
        return departmentService
                .getDepartments()
                .map{ department -> RestDepartment.of(department)}
    }

    @GetMapping("/department/{id")
    fun getDepartment(@PathVariable(value = "id") id: Long): RestDepartment {
        print("Hier wird die Methode aufgerufen")
        return departmentService
                .getDepartment(id)
                .let{ department -> RestDepartment.of(department)}
    }
}