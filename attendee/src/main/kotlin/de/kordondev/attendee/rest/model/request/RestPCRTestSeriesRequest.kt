package de.kordondev.attendee.rest.model.request

import de.kordondev.attendee.core.model.NewPCRTestSeries
import java.time.ZonedDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class RestPCRTestSeriesRequest(
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
        fun to(pcrTestSeries: RestPCRTestSeriesRequest): NewPCRTestSeries {
            return NewPCRTestSeries(
                name = pcrTestSeries.name,
                start = pcrTestSeries.start,
                end = pcrTestSeries.end,
                testCodes = pcrTestSeries.testCodes
            )
        }
    }
}