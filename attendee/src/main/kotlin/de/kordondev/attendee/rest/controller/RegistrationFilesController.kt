package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.service.RegistrationFilesService
import org.apache.commons.io.IOUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
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
    @GetMapping(value = "registrationFiles/youthPlan/{id}", produces = [ "application/pdf" ])
    fun getYouthPlan(@PathVariable(value = "id") id: Long, response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=pädagogischeBetreuer.pdf")
        return registrationFilesService.getAttendeesCommunal(id);
    }


}