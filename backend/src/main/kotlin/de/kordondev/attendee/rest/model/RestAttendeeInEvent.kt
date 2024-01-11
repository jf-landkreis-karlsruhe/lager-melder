package de.kordondev.attendee.rest.model

import de.kordondev.attendee.core.model.AttendeeInEvent
import java.time.Instant

data class RestAttendeeInEvent(
    val attendeeFirstName: String,
    val attendeeLastName: String,
    val eventName: String,
    val time: Instant
) {
    companion object {
        fun of(attendeeInEvent: AttendeeInEvent): RestAttendeeInEvent {
            return RestAttendeeInEvent(
                attendeeFirstName = attendeeInEvent.attendeeFirstName,
                attendeeLastName = attendeeInEvent.attendeeLastName,
                eventName = attendeeInEvent.eventName,
                time = attendeeInEvent.time
            )
        }
    }
}
