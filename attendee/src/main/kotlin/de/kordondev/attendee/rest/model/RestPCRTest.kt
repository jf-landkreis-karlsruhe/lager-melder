package de.kordondev.attendee.rest.model

import de.kordondev.attendee.core.persistence.entry.PCRTestEntry
import java.time.ZonedDateTime

data class RestPCRTest(
    val id: Long,
    val code: String,
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    val testedAttendees: List<RestPCRTestAttendee>
) {
    companion object {
        fun fromEntity(pcrTest: PCRTestEntry): RestPCRTest {
            return RestPCRTest(
                id = pcrTest.id,
                code = pcrTest.code,
                testedAttendees = pcrTest.testedAttendees.map { att -> RestPCRTestAttendee.of(att, pcrTest.code) },
                start = pcrTest.pcrTestSeries.start,
                end = pcrTest.pcrTestSeries.end
            )
        }
    }
}
