package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.service.AdminFilesService
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
import javax.servlet.http.HttpServletResponse

@RestController
class AdminFilesController(
    private val adminFilesService: AdminFilesService
) {

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["admin-files/events}"], produces = ["application/pdf"])
    fun getEventsPDF(@PathVariable(value = "id") id: Long, response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=events.pdf")
        return adminFilesService.createEventPDF() //TODO:create correct file
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["admin-files/batches"], produces = ["application/pdf"])
    fun getBatchPDF(response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=batches.pdf")
        return adminFilesService.createBatches()
    }
}