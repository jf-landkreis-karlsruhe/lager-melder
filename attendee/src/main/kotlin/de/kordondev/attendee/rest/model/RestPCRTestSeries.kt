package de.kordondev.attendee.rest.model

import de.kordondev.attendee.core.persistence.entry.PCRTestSeriesEntry
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

    @NotNull
    val end: ZonedDateTime,

    val testCodes: List<String>
) {
    companion object {
        fun ofEntry(pcrTestSeries: PCRTestSeriesEntry): RestPCRTestSeries {
            return RestPCRTestSeries(
                id = pcrTestSeries.id,
                name = pcrTestSeries.name,
                start = pcrTestSeries.start,
                end = pcrTestSeries.end,
                testCodes = pcrTestSeries.tests.filter { !it.trashed }.map { it.code }
            );
        }
    }
}