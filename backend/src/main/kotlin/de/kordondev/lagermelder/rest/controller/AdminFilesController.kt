package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.service.PlanningFilesService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*
import java.io.IOException

@RestController
class AdminFilesController(
    private val adminFilesService: PlanningFilesService
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

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["/admin-files/additionalInformation"], produces = ["application/pdf"])
    fun getCommentsPDF(response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=anmerkungen.pdf")
        return adminFilesService.createAdditionalInformationPDF()
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["/admin-files/overviewForDepartment"], produces = ["application/pdf"])
    fun getOverviewForDepartment(response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=übersichtÜberFeuerwehr.pdf")
        return adminFilesService.createOverviewForEachDepartment()
    }
}