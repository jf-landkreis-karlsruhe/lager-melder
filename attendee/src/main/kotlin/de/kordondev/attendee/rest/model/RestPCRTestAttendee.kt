package de.kordondev.attendee.rest.model

import de.kordondev.attendee.core.persistence.entry.AttendeeEntry

data class RestPCRTestAttendee(
    val attendeeCode: String,
    val testCode: String,
    val attendeeFirstName: String,
    val attendeeLastName: String,
    val departmentName: String
) {
    companion object {
        fun of(attendee: AttendeeEntry, pcrTestCode: String): RestPCRTestAttendee {
            return RestPCRTestAttendee(
                testCode = pcrTestCode,
                attendeeFirstName = attendee.firstName,
                attendeeLastName = attendee.lastName,
                attendeeCode = attendee.code,
                departmentName = attendee.department.name
            )
        }

    }
}