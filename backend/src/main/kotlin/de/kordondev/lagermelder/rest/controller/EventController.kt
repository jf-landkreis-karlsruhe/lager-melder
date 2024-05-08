package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.persistence.entry.EventType
import de.kordondev.lagermelder.core.service.EventService
import de.kordondev.lagermelder.rest.model.RestAttendeeInEvent
import de.kordondev.lagermelder.rest.model.RestEvent
import de.kordondev.lagermelder.rest.model.RestGlobalEventSummary
import de.kordondev.lagermelder.rest.model.request.RestEventRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
class EventController(
    private val eventService: EventService
) {
    @PostMapping("/events/by-code/{eventCode}/{attendeeCode}")
    fun addAttendeeToEvent(
        @PathVariable(value = "eventCode") eventCode: String,
        @PathVariable(value = "attendeeCode") attendeeCode: String
    ): RestAttendeeInEvent {
        return eventService.addAttendeeToEvent(eventCode, attendeeCode).let {
            RestAttendeeInEvent.of(it)
        }
    }

    @PostMapping("/events/add-department/{eventCode}/{departmentId}")
    fun addDepartmentToEvent(
        @PathVariable(value = "eventCode") eventCode: String,
        @PathVariable(value = "departmentId") departmentId: Long
    ): List<RestAttendeeInEvent> {
        return eventService.addDepartmentToEvent(eventCode, departmentId).map {
            RestAttendeeInEvent.of(it)
        }
    }

    @GetMapping("/events/by-type/{eventType}")
    fun getEventByType(@PathVariable(value = "eventType") eventType: EventType): RestEvent {
        return eventService.getEventByType(eventType)
            .let {RestEvent.of(it) }
    }

    @GetMapping("/events/by-code/{eventCode}")
    fun getEventByCode(@PathVariable(value = "eventCode") eventCode: String): RestEvent {
        return eventService.getEventByCode(eventCode)
            .let { RestEvent.of(it) }
    }

    @GetMapping("/events/global/summary")
    fun getGlobalEventSummary(): RestGlobalEventSummary {
        return eventService.getGlobalEventSummary()
    }

    @GetMapping("/events")
    fun getEvents(): List<RestEvent> {
        return eventService.getEvents()
            .map { RestEvent.of(it) }
    }

    @GetMapping("/events/{id}")
    fun getEvent(@PathVariable(value = "id") id: Long): RestEvent {
        return eventService.getEvent(id)
            .let { RestEvent.of(it) }
    }

    @PostMapping("/events")
    fun addEvent(@RequestBody(required = true) @Valid event: RestEventRequest): RestEvent {
        return eventService.createEvent(RestEventRequest.to(event))
            .let { RestEvent.of(it) }
    }

    @PutMapping("/events/{id}")
    fun saveEvent(
        @RequestBody(required = true) @Valid event: RestEventRequest,
        @PathVariable("id") id: Long
    ): RestEvent {
        return eventService.saveEvent(id, RestEventRequest.to(event))
            .let { RestEvent.of(it) }
    }

    @DeleteMapping("/events/{id}")
    fun deleteEvent(@PathVariable(value = "id") id: Long) {
        eventService.deleteEvent(id)
    }
}