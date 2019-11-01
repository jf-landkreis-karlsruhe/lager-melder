package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.service.AttendeeService
import de.kordondev.attendee.rest.model.RestAttendee
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicLong

@RestController
class AttendeeController(
        private val attendeeService: AttendeeService
) {
    val counter = AtomicLong()

    @GetMapping("/attendee")
    fun getAttendees(): List<RestAttendee> {
        return attendeeService.getAttendees().map { attendee -> RestAttendee(
                id = attendee.id,
                firstName = attendee.firstName,
                lastName = attendee.lastName
        ) }
    }

    @GetMapping("/attendee/{id}")
    fun getAttendee(@PathVariable(value = "id") id: Long): RestAttendee {
        val attendee = attendeeService.getAttendee(id);
        return RestAttendee(
                id = attendee.id,
                firstName = attendee.firstName,
                lastName = attendee.lastName
        )
    }
}