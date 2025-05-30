package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.service.AttendeeService
import de.kordondev.lagermelder.rest.model.RestRoleCount
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PublicController(
    val attendeeService: AttendeeService
) {
    @GetMapping("/public/present-by-executed-role")
    fun getPresentByExecutedRoleCount(): RestRoleCount {
        return attendeeService.getPresentByExecutedRoleCount()
            .let { RestRoleCount.of(it) }
    }
}