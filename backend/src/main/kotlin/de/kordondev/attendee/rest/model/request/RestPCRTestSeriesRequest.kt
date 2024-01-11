package de.kordondev.attendee.rest.model.request

import de.kordondev.attendee.core.persistence.entry.PCRTestSeriesEntry
import de.kordondev.attendee.rest.model.RestPCRTestSeries
import java.time.ZonedDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class RestPCRTestSeriesRequest(
    @NotNull
    @NotBlank
    val name: String,

    @NotNull
    val start: ZonedDateTime,

    @NotNull
    val end: ZonedDateTime,

    val testCodes: List<String>
) {
    companion object {
        fun of(pcrTestSeries: RestPCRTestSeries): RestPCRTestSeriesRequest {
            return RestPCRTestSeriesRequest(
                name = pcrTestSeries.name,
                start = pcrTestSeries.start,
                end = pcrTestSeries.end,
                testCodes = pcrTestSeries.testCodes
            )
        }

        fun toEntry(pcrTestSeries: RestPCRTestSeriesRequest): PCRTestSeriesEntry {
            return PCRTestSeriesEntry(
                id = 0,
                name = pcrTestSeries.name,
                start = pcrTestSeries.start,
                end = pcrTestSeries.end,
                tests = mutableSetOf(),
                trashed = false
            )
        }
    }
}