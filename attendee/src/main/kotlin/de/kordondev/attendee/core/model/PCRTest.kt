package de.kordondev.attendee.core.model

data class PCRTest(
    val id: Long,
    val code: String,
    val testedAttendees: MutableSet<Attendee>,
    val pcrTestSeries: PCRTestSeries
)