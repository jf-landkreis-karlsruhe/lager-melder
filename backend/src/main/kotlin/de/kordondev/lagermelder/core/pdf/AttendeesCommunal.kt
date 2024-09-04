package de.kordondev.lagermelder.core.pdf

import de.kordondev.lagermelder.core.pdf.PDFHelper.Companion.germanDate
import de.kordondev.lagermelder.core.persistence.entry.AttendeeRole
import de.kordondev.lagermelder.core.persistence.entry.SettingsEntry
import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
import de.kordondev.lagermelder.core.service.SettingsService
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import org.apache.pdfbox.pdmodel.interactive.form.PDField
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service

@Service
class AttendeesCommunal(
    private val resourceLoader: ResourceLoader,
    private val pdfHelper: PDFHelper,
    private val settingsService: SettingsService
) {
    private val logger: Logger = LoggerFactory.getLogger(AttendeesCommunal::class.java)

    val NAME_JFW = "Name_JF"
    val EVENT_LOCATION = "OrtdesZL"
    val START_DATE = "ZL_Start"
    val END_DATE = "ZL_Ende"
    val NAME = "T"
    val YOUTH_LEADER = "J"

    val TABLE_ROW_START_FIRST_PAGE = (1..30).toList()
    val TABLE_ROW_START_SECOND_PAGE = (31..50).toList()
    val ATTENDEES_ON_FIRST_PAGE = TABLE_ROW_START_FIRST_PAGE.size
    val ATTENDEES_ON_SECOND_PAGE = TABLE_ROW_START_SECOND_PAGE.size

    fun createAttendeesCommunalPdf(attendees: List<Attendee>, departmentName: String): PDDocument {
        val resource: Resource = resourceLoader.getResource("classpath:data/attendeesCommunal.pdf")
        val settings = settingsService.getSettings()

        logger.info("attendees ${attendees.size}")
        val result = PDDocument()
        val fields = mutableListOf<PDField>()

        var page = 1
        var lastAttendeeIndex = 0
        val pdfDocument = PDDocument.load(resource.inputStream)

        if (attendees.isEmpty()) {
            fields.addAll(fillFirstPage(pdfDocument, departmentName, attendees, page))
            result.addPage(pdfDocument.getPage(0))
        }

        for (i in 0..attendees.size - ATTENDEES_ON_SECOND_PAGE step ATTENDEES_ON_FIRST_PAGE) {

            val pdfDocument = PDDocument.load(resource.inputStream)

            val attendeesForPage = if (attendees.size <= i + ATTENDEES_ON_FIRST_PAGE) {
                lastAttendeeIndex = attendees.size
                attendees.subList(i, attendees.size)
            } else {
                lastAttendeeIndex = i + ATTENDEES_ON_FIRST_PAGE
                attendees.subList(i, i + ATTENDEES_ON_FIRST_PAGE)
            }
            fields.addAll(fillFirstPage(pdfDocument, departmentName, attendeesForPage, page))
            result.addPage(pdfDocument.getPage(0))
            page++
        }

        if (attendees.size <= ATTENDEES_ON_FIRST_PAGE) {
            val pdfDocument = PDDocument.load(resource.inputStream)
            fields.addAll(fillFirstPage(pdfDocument, departmentName, attendees, page))
            result.addPage(pdfDocument.getPage(0))
            page++
            lastAttendeeIndex = attendees.size
        }

        val attendeesForPage = attendees.subList(lastAttendeeIndex, attendees.size)
        fields.addAll(fillSecondPage(pdfDocument, attendeesForPage, page, settings))
        result.addPage(pdfDocument.getPage(1))


        val finalForm = PDAcroForm(result)
        result.documentCatalog.acroForm = finalForm
        finalForm.fields = fields
        finalForm.needAppearances = true
        return result
    }

    fun fillFirstPage(
        pdfDocument: PDDocument,
        departmentName: String,
        attendees: List<Attendee>,
        page: Int
    ): MutableList<PDField> {
        val fields = mutableListOf<PDField>()
        val form = pdfDocument.documentCatalog.acroForm;
        pdfHelper.fillField(form, NAME_JFW, departmentName, page)?.let { fields.add(it) }
        fields.addAll(fillPage(pdfDocument, attendees, TABLE_ROW_START_FIRST_PAGE, page))
        return fields
    }

    fun fillSecondPage(
        pdfDocument: PDDocument,
        attendees: List<Attendee>,
        page: Int,
        settings: SettingsEntry
    ): MutableList<PDField> {
        val fields = mutableListOf<PDField>()
        val form = pdfDocument.documentCatalog.acroForm;
        pdfHelper.fillField(form, EVENT_LOCATION, settings.hostCity, page)?.let { fields.add(it) }
        pdfHelper.fillField(form, START_DATE, settings.eventStart.format(germanDate), page)?.let { fields.add(it) }
        pdfHelper.fillField(form, END_DATE, settings.eventEnd.format(germanDate), page)?.let { fields.add(it) }
        fields.addAll(fillPage(pdfDocument, attendees, TABLE_ROW_START_SECOND_PAGE, page))
        return fields
    }

    fun fillPage(
        pdfDocument: PDDocument,
        attendees: List<Attendee>,
        cellIds: List<Int>,
        page: Int
    ): MutableList<PDField> {
        val fields = mutableListOf<PDField>()
        val form = pdfDocument.documentCatalog.acroForm;
        for (i in attendees.indices) {
            fields.addAll(fillAttendeeInForm(attendees[i], form, cellIds[i], page))
        }
        return fields
    }

    fun fillAttendeeInForm(attendee: Attendee, form: PDAcroForm, cellId: Int, page: Int): List<PDField> {
        val fields = mutableListOf<PDField>()
        pdfHelper.fillField(form, "$NAME$cellId", "${attendee.firstName} ${attendee.lastName}", page)
            ?.let { fields.add(it) }
        if (attendee.role == AttendeeRole.YOUTH_LEADER) {
            pdfHelper.checkField(form, "$YOUTH_LEADER$cellId", page)?.let { fields.add(it) }
        }
        return fields
    }

}