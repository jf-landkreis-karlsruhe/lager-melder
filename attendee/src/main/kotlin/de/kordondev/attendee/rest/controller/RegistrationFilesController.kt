package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.service.RegistrationFilesService
import de.kordondev.attendee.rest.model.RestAttendee
import de.kordondev.attendee.rest.model.YouthPlanDistribution
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
import javax.servlet.http.HttpServletResponse


@RestController
class RegistrationFilesController(
    private val registrationFilesService: RegistrationFilesService
) {

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["registrationFiles/attendeesKarlsruhe/{id}"], produces = ["application/pdf"])
    fun getAttendeesKarlsruhe(@PathVariable(value = "id") id: Long, response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=teilnehmenlisteKarlsruhe.pdf")
        return registrationFilesService.getAttendeesKarlsruhe(id);
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["registrationFiles/stateYouthPlanLeader/{id}"], produces = ["application/pdf"])
    fun getStateYouthPlanLeader(@PathVariable(value = "id") id: Long, response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=betreuerBadenWürttemberg.pdf")
        return registrationFilesService.getStateYouthPlanLeader(id);
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["registrationFiles/stateYouthPlanAttendees/{id}"], produces = ["application/pdf"])
    fun getStateYouthPlanAttendees(@PathVariable(value = "id") id: Long, response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=teilnehmenlisteBadenWürttemberg.pdf")
        return registrationFilesService.getStateYouthPlanAttendees(id);
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["registrationFiles/attendeesCommunal/{id}"], produces = ["application/pdf"])
    fun getAttendeesCommunal(@PathVariable(value = "id") id: Long, response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=teilnehmerKommandant.pdf")
        return registrationFilesService.getAttendeesCommunal(id);
    }

    @GetMapping("/registrationFiles/youthPlanDistribution")
    fun getYouthPlanDistribution(): YouthPlanDistribution {
        return registrationFilesService.getYouthPlanDistribution()
    }
}