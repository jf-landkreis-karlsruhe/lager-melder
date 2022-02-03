package de.kordondev.attendee.rest.model

import de.kordondev.attendee.core.model.PCRTest
import java.time.ZonedDateTime

data class RestPCRTest(
    val id: Long,
    val code: String,
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    val testedAttendees: List<RestPCRTestAttendee>
) {
    companion object {
        fun of(pcrTest: PCRTest): RestPCRTest {
            return RestPCRTest(
                id = pcrTest.id,
                code = pcrTest.code,
                testedAttendees = pcrTest.testedAttendees.map { RestPCRTestAttendee.of(it) }
            )
        }
    }
}
