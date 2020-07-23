package de.kordondev.attendee.core.pdf

import de.kordondev.attendee.core.model.Attendee
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import org.apache.pdfbox.pdmodel.interactive.form.PDField
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader

class YouthLeader(
        val resourceLoader: ResourceLoader,
        val pdfHelper: PDFHelper,
        @Value("\${data.kreiszeltlager.from}") private val eventStart: String,
        @Value("\${data.kreiszeltlager.to}") private val eventEnd: String,
        @Value("\${data.kreiszeltlager.year}") private val dataYear: String,
        @Value("\${data.kreiszeltlager.listBW.eventAddress}") private val dataEventAddress: String,
        @Value("\${data.kreiszeltlager.listBW.organisationAddress}") private val dataOrganisationAddress: String

) {
    private val logger: Logger = LoggerFactory.getLogger(YouthLeader::class.java)

    val SUM_DAYS_PAGE_1 = "tage_gesamt"
    val COPY_SUM_DAYS_PAGE_1_TO_PAGE_2 = "Texteingabe510"
    val SUM_DAYS_PAGE_2 = "Texteingabe500"
    val YEAR = "Landesjugendplan"
    val ORGANISATION_ADDRESS = "Adresse"
    val CATEGORY = "Art_der_Massnahme"
    val START_DATE = "Beginn_der_Massnahme"
    val END_DATE = "Ende_der_Massnahme"
    val EVENT_ADDRESS = "Ort_der_Massnahme"
    val TABLE_ROW_START = listOf(96, 106, 116, 126, 136, 146, 156, 166, 176, 186, 196, 206, 216, 239, 250)
    val ATTENDEES_ON_PAGE = 15

    val DAYS_OF_EVENT = 5

    fun createYouthLeaderPdf(attendees: List<Attendee>): PDDocument {
        val resource: Resource = resourceLoader.getResource("classpath:data/youthLeader.pdf")

        logger.info("attendeeSize ${attendees.size}")

        val result = PDDocument()
        val fields = mutableListOf<PDField>()
        val pdfDocument = PDDocument.load(resource.inputStream)
        val form = pdfDocument.documentCatalog.acroForm;
        form.fields.map { logger.info(it.fullyQualifiedName) }

        /*
        var page = 1
        for (i in ATTENDEES_ON_PAGE until attendees.size step ATTENDEES_ON_PAGE) {

            val pdfDocument = PDDocument.load(resource.inputStream)

            val attendeesForPage = if (attendees.size <= i + ATTENDEES_ON_PAGE) {
                attendees.subList(i, attendees.size)
            } else {
                attendees.subList(i, i + ATTENDEES_ON_PAGE)
            }
            fields.addAll(fillPage(pdfDocument, attendeesForPage, TABLE_ROW_START, page))
            result.addPage(pdfDocument.getPage(1))
            page++
        }*/

        val finalForm = PDAcroForm(result)
        result.documentCatalog.acroForm = finalForm
        finalForm.fields = fields
        finalForm.needAppearances = true
        return result
    }


    fun fillPage(pdfDocument: PDDocument, attendees: List<Attendee>, cellIds: List<Int>, page: Int): MutableList<PDField> {
        val fields = mutableListOf<PDField>()
        val form = pdfDocument.documentCatalog.acroForm;
        for (i in attendees.indices) {
            fields.addAll(fillAttendeeInForm(attendees[i], form, cellIds[i], page))
        }
        return fields
    }

    fun fillAttendeeInForm(attendee: Attendee, form: PDAcroForm, firstCellId: Int, page: Number): List<PDField> {
        val fields = mutableListOf<PDField>()
        val nameCellId = firstCellId + 2
        val birthDateCellId = firstCellId + 4
        val startCellId = firstCellId + 5
        val endCellId = firstCellId + 6
        val daysCellId = firstCellId + 7
        pdfHelper.fillField(form, "Texteingabe$nameCellId", "${attendee.lastName}, ${attendee.firstName}", page)?.let { fields.add(it) }
        pdfHelper.fillField(form, "Texteingabe$birthDateCellId", attendee.birthday, page)?.let { fields.add(it) }
        pdfHelper.fillField(form, "Texteingabe$startCellId", eventStart, page)?.let { fields.add(it) }
        pdfHelper.fillField(form, "Texteingabe$endCellId", eventEnd, page)?.let { fields.add(it) }
        pdfHelper.fillField(form, "Texteingabe$daysCellId", "$DAYS_OF_EVENT", page)?.let { fields.add(it) }
        return fields
    }
}