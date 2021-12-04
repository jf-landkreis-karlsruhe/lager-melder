package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.service.EventService
import de.kordondev.attendee.rest.model.RestAttendeeInEvent
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class EventController(
    private val eventService: EventService
) {
    @PostMapping("events/{eventCode}/{attendeeCode}")
    fun addAttendeeToEvent(
        @PathVariable(value = "eventCode") eventCode: String,
        @PathVariable(value = "attendeeCode") attendeeCode: String
    ): RestAttendeeInEvent {
        return eventService.addAttendeeToEvent(eventCode, attendeeCode).let {
            RestAttendeeInEvent.of(it)
        }
    }
}