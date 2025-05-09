package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.service.PlanningFilesService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
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
    fun getBatchPDFOrderedByDepartment(response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=batches.pdf")
        return planningFilesService.createBatchesOrderedByDepartment()
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["/planning-files/batches-ordered-by-creation-date"], produces = ["application/pdf"])
    fun getBatchPDFOrderedByCreationDate(response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=batches.pdf")
        return planningFilesService.createBatchesOrderedByCreationDate()
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

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["/planning-files/missing-juleika"], produces = ["application/pdf"])
    fun getMissingJuleika(response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=fehlendeJuleika.pdf")
        return planningFilesService.createMissingJuleika()
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["/planning-files/tents-and-duties-csv"], produces = ["text/csv"])
    fun getTentsAndDutiesCSV(response: HttpServletResponse): String? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=Zeltlager.csv")
        return planningFilesService.createTentsAndDutiesCSV()
    }
}
