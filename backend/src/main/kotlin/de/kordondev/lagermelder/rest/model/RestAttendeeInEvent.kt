package de.kordondev.lagermelder.rest.model

import de.kordondev.lagermelder.core.service.helper.AttendeeInEvent
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
