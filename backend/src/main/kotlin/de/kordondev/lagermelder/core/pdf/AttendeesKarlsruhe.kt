package de.kordondev.lagermelder.core.pdf

import de.kordondev.lagermelder.core.model.Attendee
import de.kordondev.lagermelder.core.model.Settings
import de.kordondev.lagermelder.core.pdf.PDFHelper.Companion.germanDate
import de.kordondev.lagermelder.core.pdf.PDFHelper.Companion.germanDateShort
import de.kordondev.lagermelder.core.persistence.entry.AttendeeRole
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
class AttendeesKarlsruhe(
    private val resourceLoader: ResourceLoader,
    private val pdfHelper: PDFHelper,
    private val settingsService: SettingsService
) {
    private val logger: Logger = LoggerFactory.getLogger(AttendeesKarlsruhe::class.java)

    private val TABLE_ROW_START_PAGE_1 = (1..16).toList()
    private val TABLE_ROW_START_PAGE_2 = (17..35).toList()
    private val ATTENDEES_ON_FIRST_PAGE = TABLE_ROW_START_PAGE_1.size
    private val ATTENDEES_ON_SECOND_PAGE = TABLE_ROW_START_PAGE_2.size
    private val ORGANISATION = "für"
    private val LOCATION = "in"
    private val EVENT_START = "vom"
    private val EVENT_END = "bis"
    private val DAYS_OF_EVENT = 5

    fun createAttendeesKarlsruhePdf(attendees: List<Attendee>): PDDocument {
        val resource: Resource = resourceLoader.getResource("classpath:data/attendees_LRA_KA.pdf")
        val settings = settingsService.getSettings()

        logger.info("attendeeSize ${attendees.size}")

        val result = PDDocument()
        val fields = mutableListOf<PDField>()

        if (attendees.size <= ATTENDEES_ON_FIRST_PAGE) {
            val pdfDocument = PDDocument.load(resource.inputStream)
            fields.addAll(fillPage(pdfDocument, attendees, TABLE_ROW_START_PAGE_1, 1000, settings))
            fields.addAll(fillFirstPage(pdfDocument, attendees, 1000, settings))
            result.addPage(pdfDocument.getPage(0))

        } else {
            var pdfDocument = PDDocument.load(resource.inputStream)
            fields.addAll(
                fillPage(
                    pdfDocument,
                    attendees.subList(0, ATTENDEES_ON_FIRST_PAGE),
                    TABLE_ROW_START_PAGE_1,
                    1000,
                    settings
                )
            )
            fields.addAll(fillFirstPage(pdfDocument, attendees, 1000, settings))
            result.addPage(pdfDocument.getPage(0))
            var page = 1002
            for (i in ATTENDEES_ON_FIRST_PAGE until attendees.size step ATTENDEES_ON_SECOND_PAGE) {

                pdfDocument = PDDocument.load(resource.inputStream)

                val attendeesForPage = if (attendees.size <= i + ATTENDEES_ON_SECOND_PAGE) {
                    attendees.subList(i, attendees.size)
                } else {
                    attendees.subList(i, i + ATTENDEES_ON_SECOND_PAGE)
                }
                fields.addAll(fillPage(pdfDocument, attendeesForPage, TABLE_ROW_START_PAGE_2, page, settings))
                result.addPage(pdfDocument.getPage(1))
                page++
            }
        }

        val finalForm = PDAcroForm(result)
        result.documentCatalog.acroForm = finalForm
        finalForm.fields = fields
        finalForm.needAppearances = true
        return result
    }

    fun fillPage(
        pdfDocument: PDDocument,
        attendees: List<Attendee>,
        cellIds: List<Int>,
        page: Int,
        settings: Settings
    ): MutableList<PDField> {
        val fields = mutableListOf<PDField>()
        val form = pdfDocument.documentCatalog.acroForm
        for (i in attendees.indices) {
            fields.addAll(fillAttendeeInForm(attendees[i], form, cellIds[i], page, settings))
        }
        return fields
    }

    fun fillFirstPage(
        pdfDocument: PDDocument,
        attendees: List<Attendee>,
        page: Int,
        settings: Settings
    ): MutableList<PDField> {
        val fields = mutableListOf<PDField>()
        val form = pdfDocument.documentCatalog.acroForm
        pdfHelper.fillField(form, ORGANISATION, settings.eventName, page)?.let { fields.add(it) }
        pdfHelper.fillField(form, LOCATION, settings.eventAddress, page)?.let { fields.add(it) }
        pdfHelper.fillField(form, EVENT_START, settings.eventStart.format(germanDate), page)
            ?.let { fields.add(it) }
        pdfHelper.fillField(form, EVENT_END, settings.eventEnd.format(germanDate), page)?.let { fields.add(it) }
        return fields
    }

    fun fillAttendeeInForm(
        attendee: Attendee,
        form: PDAcroForm,
        firstCellId: Int,
        page: Number,
        settings: Settings
    ): List<PDField> {
        val fields = mutableListOf<PDField>()
        val nameCellId = firstCellId
        var birthDateCellId = firstCellId + 35
        if (firstCellId in 33..35) {
            birthDateCellId += 1
        }
        var startCellId = firstCellId + 300
        if (firstCellId == 13) {
            startCellId = 352
        }
        if (firstCellId in 14..35) {
            startCellId -= 1
        }
        var endCellId = firstCellId + 400
        if (firstCellId == 16) {
            endCellId = 455
        }
        if (firstCellId in 17..35) {
            endCellId -= 1
        }
        val daysCellId = firstCellId + 499
        val youthCellId = firstCellId + 599
        val youthLeaderCellId = firstCellId + 699
        pdfHelper.fillField(form, "$nameCellId", "${attendee.lastName}, ${attendee.firstName}", page)
            ?.let { fields.add(it) }
        pdfHelper.fillField(form, "$birthDateCellId", pdfHelper.formatBirthday(attendee.birthday, germanDate), page)?.let { fields.add(it) }
        pdfHelper.fillField(form, "$startCellId", settings.eventStart.format(germanDateShort), page)
            ?.let { fields.add(it) }
        pdfHelper.fillField(form, "$endCellId", settings.eventEnd.format(germanDateShort), page)?.let { fields.add(it) }
        pdfHelper.fillField(form, "$daysCellId", "$DAYS_OF_EVENT", page)?.let { fields.add(it) }
        when (attendee.role) {
            AttendeeRole.YOUTH -> pdfHelper.fillField(form, "$youthCellId", "x", page)?.let { fields.add(it) }
            AttendeeRole.YOUTH_LEADER -> pdfHelper.fillField(form, "$youthLeaderCellId", "x", page)
                ?.let { fields.add(it) }
        }
        return fields
    }
}
