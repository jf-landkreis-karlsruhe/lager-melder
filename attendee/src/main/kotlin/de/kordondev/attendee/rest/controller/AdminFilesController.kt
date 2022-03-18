package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.service.AdminFilesService
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
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
    @GetMapping(value = ["/admin-files/events"], produces = ["application/pdf"])
    fun getEventsPDF(
        @RequestParam(value = "frontendBaseUrl") frontendBaseUrl: String,
        response: HttpServletResponse
    ): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=events.pdf")
        return adminFilesService.createEventPDF(frontendBaseUrl)
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["/admin-files/batches"], produces = ["application/pdf"])
    fun getBatchPDF(response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=batches.pdf")
        return adminFilesService.createBatches()
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["/admin-files/t-shirts"], produces = ["application/pdf"])
    fun getTShirtPDF(response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=tshirts.pdf")
        return adminFilesService.createTShirtPDF()
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["/admin-files/food"], produces = ["application/pdf"])
    fun getFoodPDF(response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=essen.pdf")
        return adminFilesService.createFoodPDF()
    }
}