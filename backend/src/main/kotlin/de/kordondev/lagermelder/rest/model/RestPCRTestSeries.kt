package de.kordondev.lagermelder.rest.model

import de.kordondev.lagermelder.core.persistence.entry.PCRTestSeriesEntry
import de.kordondev.lagermelder.rest.model.request.RestPCRTestSeriesRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.ZonedDateTime

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

        fun ofRequest(pcrTestSeries: RestPCRTestSeriesRequest, id: Long): RestPCRTestSeries {
            return RestPCRTestSeries(
                id = id,
                name = pcrTestSeries.name,
                start = pcrTestSeries.start,
                end = pcrTestSeries.end,
                testCodes = pcrTestSeries.testCodes
            );

        }
    }
}