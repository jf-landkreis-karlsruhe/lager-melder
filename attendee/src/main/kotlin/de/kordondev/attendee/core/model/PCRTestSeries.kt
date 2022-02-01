package de.kordondev.attendee.core.model

import java.time.ZonedDateTime

data class PCRTestSeries(
    val id: Long,
    val name: String,
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    val tests: List<PCRTest>
)

data class NewPCRTestSeries(
    val name: String,
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    val testCodes: List<String>
)
