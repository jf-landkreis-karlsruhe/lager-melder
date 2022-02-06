package de.kordondev.attendee.core.model

import java.time.ZonedDateTime

data class PCRTest(
    val id: Long,
    val code: String,
    val testedAttendees: MutableSet<Attendee>,
    val pcrTestSeriesId: String,
    val start: ZonedDateTime,
    val end: ZonedDateTime,
)