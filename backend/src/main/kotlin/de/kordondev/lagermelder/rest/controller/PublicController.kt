package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.service.EventService
import de.kordondev.lagermelder.rest.model.RestRoleCount
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PublicController(
    private val eventService: EventService
) {
    @GetMapping("/public/present-by-executed-role")
    fun getPresentByExecutedRoleCount(): RestRoleCount {
        return eventService.getGlobalEventSummary()
            .let { RestRoleCount.of(it) }
    }
}