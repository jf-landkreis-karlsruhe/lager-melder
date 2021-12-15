package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.service.EventService
import de.kordondev.attendee.rest.model.RestAttendeeInEvent
import de.kordondev.attendee.rest.model.RestEvent
import de.kordondev.attendee.rest.model.request.RestEventRequest
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class EventController(
    private val eventService: EventService
) {
    @PostMapping("events/by-code/{eventCode}/{attendeeCode}")
    fun addAttendeeToEvent(
        @PathVariable(value = "eventCode") eventCode: String,
        @PathVariable(value = "attendeeCode") attendeeCode: String
    ): RestAttendeeInEvent {
        return eventService.addAttendeeToEvent(eventCode, attendeeCode).let {
            RestAttendeeInEvent.of(it)
        }
    }

    @GetMapping("events/by-code/{eventCode}")
    fun getEventByCode(@PathVariable(value = "eventCode") eventCode: Long): RestEvent {
        return eventService.getEvent(eventCode)
            .let { RestEvent.of(it) }
    }

    @GetMapping("events")
    fun getEvents(): List<RestEvent> {
        return eventService.getEvents()
            .map { RestEvent.of(it) }
    }

    @GetMapping("events/{id}")
    fun getEvent(@PathVariable(value = "id") id: Long): RestEvent {
        return eventService.getEvent(id)
            .let { RestEvent.of(it) }
    }

    @PostMapping("/events")
    fun addEvent(@RequestBody(required = true) @Valid event: RestEventRequest): RestEvent {
        return eventService.createEvent(RestEventRequest.to(event))
            .let { RestEvent.of(it) }
    }

    @PutMapping("/event/{id}")
    fun saveEvent(
        @RequestBody(required = true) @Valid event: RestEventRequest,
        @PathVariable("id") id: Long
    ): RestEvent {
        return eventService.saveEvent(id, RestEventRequest.to(event))
            .let { RestEvent.of(it) }
    }

    @DeleteMapping("/event/{id}")
    fun deleteEvent(@PathVariable(value = "id") id: Long) {
        eventService.deleteEvent(id)
    }
}