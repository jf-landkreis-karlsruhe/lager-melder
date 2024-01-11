package de.kordondev.attendee.core.model

import java.time.Instant

data class NewAttendeeCodeInEventCode(
    val attendeeCode: String,
    val eventCode: String,
    val time: Instant
)

data class AttendeeInEvent(
    val attendeeFirstName: String,
    val attendeeLastName: String,
    val eventName: String,
    val time: Instant
)