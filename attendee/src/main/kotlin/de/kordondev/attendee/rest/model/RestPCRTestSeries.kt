package de.kordondev.attendee.rest.model

import de.kordondev.attendee.core.model.PCRTest
import de.kordondev.attendee.core.model.PCRTestSeries
import java.time.ZonedDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class RestPCRTestSeries(
    val id: Long,

    @NotNull
    @NotBlank
    val name: String,

    @NotNull
    val start: ZonedDateTime,

    // TODO: After start
    @NotNull
    val end: ZonedDateTime,

    val testCodes: List<String>
) {
    companion object {
        fun of(pcrTestSeries: PCRTestSeries): RestPCRTestSeries {
            return RestPCRTestSeries(
                id = pcrTestSeries.id,
                name = pcrTestSeries.name,
                start = pcrTestSeries.start,
                end = pcrTestSeries.end,
                testCodes = pcrTestSeries.tests.map { it.code }
            );
        }

        fun to(pcrTestSeries: RestPCRTestSeries, pcrTests: Set<PCRTest>): PCRTestSeries {
            return PCRTestSeries(
                id = pcrTestSeries.id,
                name = pcrTestSeries.name,
                start = pcrTestSeries.start,
                end = pcrTestSeries.end,
                tests = pcrTests.toList()
            )
        }
    }
}