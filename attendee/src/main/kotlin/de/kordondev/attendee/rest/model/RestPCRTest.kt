package de.kordondev.attendee.rest.model

import de.kordondev.attendee.core.model.PCRTest

data class RestPCRTest(
    val id: Long,
    val code: String,
    val testedAttendees: Set<SlimRestAttendee>
) {
    companion object {
        fun of(pcrTest: PCRTest): RestPCRTest {
            return RestPCRTest(
                id = pcrTest.id,
                code = pcrTest.code,
                testedAttendees = pcrTest.testedAttendees.map { SlimRestAttendee.of(it) }.toSet()
            )
        }
    }
}
