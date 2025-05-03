package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.service.RegistrationFilesService
import de.kordondev.lagermelder.core.service.models.Group
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*
import java.io.IOException


@RestController
class RegistrationFilesController(
    private val registrationFilesService: RegistrationFilesService
) {

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["registrationFiles/attendeesKarlsruhe/{id}"], produces = ["application/pdf"])
    fun getAttendeesKarlsruhe(
        @PathVariable(value = "id") id: Long,
        @RequestParam("group") requestGroup: String,
        response: HttpServletResponse
    ): ByteArray? {
        val group = Group.getGroup(requestGroup)
        response.addHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment;filename=teilnehmerlisteKarlsruhe-${group.name}.pdf"
        )
        return registrationFilesService.getAttendeesKarlsruhe(id, group)
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["registrationFiles/stateYouthPlanLeader/{id}"], produces = ["application/pdf"])
    fun getStateYouthPlanLeader(
        @PathVariable(value = "id") id: Long,
        @RequestParam("group") requestGroup: String,
        response: HttpServletResponse
    ): ByteArray? {
        val group = Group.getGroup(requestGroup)
        response.addHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment;filename=betreuerBadenWürttemberg-${group.name}.pdf"
        )
        return registrationFilesService.getStateYouthPlanLeader(id, group)
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["registrationFiles/stateYouthPlanAttendees/{id}"], produces = ["application/pdf"])
    fun getStateYouthPlanAttendees(
        @PathVariable(value = "id") id: Long, response: HttpServletResponse,
        @RequestParam("group") requestGroup: String
    ): ByteArray? {
        val group = Group.getGroup(requestGroup)
        response.addHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment;filename=teilnehmerlisteBadenWürttemberg-${group.name}.pdf"
        )
        return registrationFilesService.getStateYouthPlanYouth(id, group)
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["registrationFiles/attendeesCommunal/{id}"], produces = ["application/pdf"])
    fun getAttendeesCommunal(@PathVariable(value = "id") id: Long, response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=teilnehmerKommandant.pdf")
        return registrationFilesService.getAttendeesCommunal(id)
    }

}