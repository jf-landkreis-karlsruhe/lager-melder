package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.service.PlanningFilesService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*
import java.io.IOException

@RestController
class PlanningFilesController(
    private val planningFilesService: PlanningFilesService
) {

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["/planning-files/events"], produces = ["application/pdf"])
    fun getEventsPDF(
        @RequestParam(value = "frontendBaseUrl") frontendBaseUrl: String,
        response: HttpServletResponse
    ): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=events.pdf")
        return planningFilesService.createEventPDF(frontendBaseUrl)
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["/planning-files/batches"], produces = ["application/pdf"])
    fun getBatchPDF(response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=batches.pdf")
        return planningFilesService.createBatches()
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["/planning-files/t-shirts"], produces = ["application/pdf"])
    fun getTShirtPDF(response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=tshirts.pdf")
        return planningFilesService.createTShirtPDF()
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["/planning-files/food"], produces = ["application/pdf"])
    fun getFoodPDF(response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=essen.pdf")
        return planningFilesService.createFoodPDF()
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["/planning-files/additionalInformation"], produces = ["application/pdf"])
    fun getCommentsPDF(response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=anmerkungen.pdf")
        return planningFilesService.createAdditionalInformationPDF()
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["/planning-files/overviewForDepartment"], produces = ["application/pdf"])
    fun getOverviewForDepartment(response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=übersichtÜberFeuerwehr.pdf")
        return planningFilesService.createOverviewForEachDepartment()
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["/planning-files/contactOverview"], produces = ["application/pdf"])
    fun getContactOverview(response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=kontaktdatenÜbersicht.pdf")
        return planningFilesService.createContactList()
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["/planning-files/tentMarkings"], produces = ["application/pdf"])
    fun getTentMarkings(response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=Zeltschilder.pdf")
        return planningFilesService.createTentMarkings()
    }
}
