package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.service.PCRTestService
import de.kordondev.attendee.rest.model.RestPCRTest
import de.kordondev.attendee.rest.model.RestPCRTestAttendee
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PCRTestController(
    private val pcrTestService: PCRTestService
) {

    @GetMapping("/pcr-tests/by-code/{code}")
    fun getPCRTestByCode(
        @PathVariable(required = true) code: String
    ): RestPCRTest {
        return pcrTestService.getPCRTestForCode2(code)
            .let {
                RestPCRTest(
                    id = it.id,
                    code = it.code,
                    testedAttendees = listOf(),
                    start = it.pcrTestSeries.start,
                    end = it.pcrTestSeries.end
                )
            }
    }

    @PostMapping("/prc-tests/by-code/{testCode}/{attendeeCode}")
    fun addAttendeeToPCRTestByCode(
        @PathVariable(required = true) testCode: String,
        @PathVariable(required = true) attendeeCode: String
    ): RestPCRTestAttendee {
        return pcrTestService.addAttendeeToPCRTest(testCode, attendeeCode)
            .let { RestPCRTestAttendee.of(it, testCode) }
    }

    /*@DeleteMapping("/prc-tests/by-code/{testCode}/{attendeeCode}")
    fun deleteAttendeeToPCRTestByCode(
        @PathVariable(required = true) testCode: String,
        @PathVariable(required = true) attendeeCode: String
    ) {
        pcrTestService.deleteAttendeeToPCRTest(testCode, attendeeCode)
    }*/
}