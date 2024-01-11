package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.service.PCRTestSeriesService
import de.kordondev.lagermelder.core.service.PCRTestService
import de.kordondev.lagermelder.rest.model.RestPCRTestSeries
import de.kordondev.lagermelder.rest.model.request.RestPCRTestSeriesRequest
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class PCRTestSeriesController(
    private val pcrTestSeriesService: PCRTestSeriesService,
    private val pcrTestService: PCRTestService
) {

    @PostMapping("/pcr-test-series")
    fun createPcrTestSeries(
        @RequestBody(required = true) @Valid pcrTestSeries: RestPCRTestSeriesRequest
    ): RestPCRTestSeries {
        return pcrTestSeriesService
            .createPcrTestSeries(pcrTestSeries)
    }

    @PutMapping("/pcr-test-series/{id}")
    fun savePcrTestSeries(
        @PathVariable(required = true) id: Long,
        @RequestBody(required = true) @Valid pcrTestSeries: RestPCRTestSeries
    ): RestPCRTestSeries {
        return pcrTestSeriesService.savePcrTestSeries(id, pcrTestSeries)
    }

    @GetMapping("/pcr-test-series")
    fun getAllPCRTestSeries(): List<RestPCRTestSeries> {
        return pcrTestSeriesService.getAllPcrTestSeries()
    }

    @GetMapping("/pcr-test-series/{id}")
    fun getPCRTestSeries(
        @PathVariable(required = true) id: Long
    ): RestPCRTestSeries {
        return pcrTestSeriesService.getPcrTestSeries(id)
    }

    @DeleteMapping("/pcr-test-series/{id}")
    fun deletePCRTestSeries(
        @PathVariable(required = true) id: Long
    ) {
        pcrTestSeriesService.deletePcrTestSeries(id)
    }
}