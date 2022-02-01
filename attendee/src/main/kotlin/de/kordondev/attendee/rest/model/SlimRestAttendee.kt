package de.kordondev.attendee.rest.model

import de.kordondev.attendee.core.model.Attendee

data class SlimRestAttendee(
    val attendeeId: Long,
    val attendeeFirstName: String,
    val attendeeLastName: String,
    val departmentName: String
) {
    companion object {
        fun of(attendee: Attendee): SlimRestAttendee {
            return SlimRestAttendee(
                attendeeId = attendee.id,
                attendeeFirstName = attendee.firstName,
                attendeeLastName = attendee.lastName,
                departmentName = attendee.department.name
            )
        }
    }
}
