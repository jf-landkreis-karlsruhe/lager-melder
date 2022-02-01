package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.service.PCRTestService
import de.kordondev.attendee.rest.model.RestPCRTest
import de.kordondev.attendee.rest.model.RestPCRTestAttendee
import org.springframework.web.bind.annotation.*

@RestController
class PCRTestController(
    private val pcrTestService: PCRTestService
) {

    @GetMapping("/pcr-tests/by-code/{code}")
    fun getPCRTestByCode(
        @PathVariable(required = true) code: String
    ): RestPCRTest {
        return pcrTestService.getPCRTestForCode(code)
            .let { RestPCRTest.of(it) }
    }

    @PostMapping("/prc-tests/by-code/{testCode}/{attendeeCode}")
    fun addAttendeeToPCRTestByCode(
        @PathVariable(required = true) testCode: String,
        @PathVariable(required = true) attendeeCode: String
    ): RestPCRTestAttendee {
        return pcrTestService.addAttendeeToPCRTest(testCode, attendeeCode)
            .let { RestPCRTestAttendee.of(it, testCode) }
    }

    @DeleteMapping("/prc-tests/by-code/{testCode}/{attendeeCode}")
    fun deleteAttendeeToPCRTestByCode(
        @PathVariable(required = true) testCode: String,
        @PathVariable(required = true) attendeeCode: String
    ) {
        pcrTestService.deleteAttendeeToPCRTest(testCode, attendeeCode)
    }
}