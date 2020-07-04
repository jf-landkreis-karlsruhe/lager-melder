package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.service.RegistrationFilesService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController/*("registrationFiles/")*/
class RegistrationFilesController(
        private val registrationFilesService: RegistrationFilesService
) {
/*
    @GetMapping("youthPlan")
    fun getYouthPlan(@PathVariable(value = "id") id: Long): ResponseEntity {

    }
*/
    @GetMapping("registrationFiles/youthPlan/{id}")
    fun getYouthPlan(@PathVariable(value = "id") id: Long) {
        registrationFilesService.getYouthPlan(id);
    }
}