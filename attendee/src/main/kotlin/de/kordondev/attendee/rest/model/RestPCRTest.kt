package de.kordondev.attendee.rest.model

import java.time.ZonedDateTime

data class RestPCRTest(
    val id: Long,
    val code: String,
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    val testedAttendees: List<RestPCRTestAttendee>
)
