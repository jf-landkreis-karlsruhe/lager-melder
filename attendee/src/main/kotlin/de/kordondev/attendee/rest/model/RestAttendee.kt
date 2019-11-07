package de.kordondev.attendee.rest.model

import de.kordondev.attendee.core.model.Attendee

data class RestAttendee(
        val id: Long,
        val firstName: String,
        val lastName: String,
        val departmentId: Long
) {
    companion object {
        fun of(attendee: Attendee) = RestAttendee(
                id = attendee.id,
                firstName = attendee.firstName,
                lastName = attendee.lastName,
                departmentId = attendee.department.id
        )
    }
}
