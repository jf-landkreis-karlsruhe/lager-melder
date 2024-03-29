package de.kordondev.lagermelder.core.pdf

import de.kordondev.lagermelder.core.pdf.PDFHelper.Companion.germanDate
import de.kordondev.lagermelder.core.persistence.entry.AttendeeEntry
import de.kordondev.lagermelder.core.persistence.entry.SettingsEntry
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
class StateYouthPlanAttendees(
    private val resourceLoader: ResourceLoader,
    private val pdfHelper: PDFHelper,
    private val settingsService: SettingsService
) {
    private val logger: Logger = LoggerFactory.getLogger(StateYouthPlanAttendees::class.java)

    val SUM_DAYS_PAGE_1 = "Tage_gesamt"
    val COPY_SUM_DAYS_PAGE_1_TO_PAGE_2 = "Texteingabe510"
    val SUM_DAYS_PAGE_2 = "Texteingabe500"
    val YEAR = "Landesjugendplan"
    val ORGANISATION_ADDRESS = "Adresse"
    val CATEGORY = "Art_der_Massnahme"
    val START_DATE = "Beginn_der_Massnahme"
    val END_DATE = "Ende_der_Massnahme"
    val EVENT_ADDRESS = "Ort_der_Massnahme"
    val TABLE_ROW_START_PAGE_1 = listOf(96, 106, 116, 126, 136, 146, 156, 166, 176, 186, 196, 206, 216, 239, 250)
    val ATTENDEES_ON_FIRST_PAGE = TABLE_ROW_START_PAGE_1.size
    val TABLE_ROW_START_PAGE_2 = listOf(
        513,
        523,
        533,
        543,
        553,
        563,
        573,
        583,
        593,
        603,
        613,
        623,
        646,
        657,
        910,
        921,
        932,
        943,
        954,
        965,
        976,
        987,
        998,
        1009,
        1020
    )
    val ATTENDEES_ON_SECOND_PAGE = TABLE_ROW_START_PAGE_2.size

    val DAYS_OF_EVENT = 5

    fun createStateYouthPlanAttendees(attendees: List<AttendeeEntry>): PDDocument {
        val resource: Resource = resourceLoader.getResource("classpath:data/stateYouthPlanAttendees.pdf")
        val settings = settingsService.getSettings()

        logger.info("attendeeSize ${attendees.size}")

        val result = PDDocument()
        val fields = mutableListOf<PDField>()

        if (attendees.size <= ATTENDEES_ON_FIRST_PAGE) {
            val pdfDocument = PDDocument.load(resource.inputStream)
            fields.addAll(fillPage(pdfDocument, attendees, TABLE_ROW_START_PAGE_1, 1, settings))
            fields.addAll(fillFirstPage(pdfDocument, attendees, 1, settings))
            result.addPage(pdfDocument.getPage(0))

        } else {
            var pdfDocument = PDDocument.load(resource.inputStream)
            fields.addAll(
                fillPage(
                    pdfDocument,
                    attendees.subList(0, ATTENDEES_ON_FIRST_PAGE),
                    TABLE_ROW_START_PAGE_1,
                    1,
                    settings
                )
            )
            fields.addAll(fillFirstPage(pdfDocument, attendees.subList(0, ATTENDEES_ON_FIRST_PAGE), 1, settings))
            result.addPage(pdfDocument.getPage(0))
            var page = 2
            for (i in ATTENDEES_ON_FIRST_PAGE until attendees.size step ATTENDEES_ON_SECOND_PAGE) {

                pdfDocument = PDDocument.load(resource.inputStream)

                val attendeesForPage = if (attendees.size <= i + ATTENDEES_ON_SECOND_PAGE) {
                    attendees.subList(i, attendees.size)
                } else {
                    attendees.subList(i, i + ATTENDEES_ON_SECOND_PAGE)
                }
                fields.addAll(fillPage(pdfDocument, attendeesForPage, TABLE_ROW_START_PAGE_2, page, settings))
                fields.addAll(fillSecondPage(pdfDocument, attendeesForPage, page))
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
        attendees: List<AttendeeEntry>,
        cellIds: List<Int>,
        page: Int,
        settings: SettingsEntry
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
        attendees: List<AttendeeEntry>,
        page: Int,
        settings: SettingsEntry
    ): MutableList<PDField> {
        val fields = mutableListOf<PDField>()
        val form = pdfDocument.documentCatalog.acroForm;
        pdfHelper.fillField(form, SUM_DAYS_PAGE_1, "${attendees.size * DAYS_OF_EVENT}", page)?.let { fields.add(it) }
        pdfHelper.fillField(form, YEAR, "${settings.eventStart.year}", page)?.let { fields.add(it) }
        pdfHelper.fillField(form, ORGANISATION_ADDRESS, "${settings.organisationAddress}", page)?.let { fields.add(it) }
        pdfHelper.fillField(form, START_DATE, "${settings.eventStart.format(germanDate)}", page)?.let { fields.add(it) }
        pdfHelper.fillField(form, END_DATE, "${settings.eventEnd.format(germanDate)}", page)?.let { fields.add(it) }
        pdfHelper.fillField(form, EVENT_ADDRESS, "${settings.eventAddress}", page)?.let { fields.add(it) }
        return fields
    }

    fun fillSecondPage(pdfDocument: PDDocument, attendees: List<AttendeeEntry>, page: Int): MutableList<PDField> {
        val fields = mutableListOf<PDField>()
        val form = pdfDocument.documentCatalog.acroForm;
        val previousDays = (ATTENDEES_ON_FIRST_PAGE + ATTENDEES_ON_SECOND_PAGE * (page - 2)) * DAYS_OF_EVENT
        pdfHelper.fillField(form, COPY_SUM_DAYS_PAGE_1_TO_PAGE_2, "$previousDays", page)?.let { fields.add(it) }
        pdfHelper.fillField(form, SUM_DAYS_PAGE_2, "${previousDays + attendees.size * DAYS_OF_EVENT}", page)
            ?.let { fields.add(it) }
        return fields
    }

    private fun fillAttendeeInForm(
        attendee: AttendeeEntry,
        form: PDAcroForm,
        firstCellId: Int,
        page: Number,
        settings: SettingsEntry
    ): List<PDField> {
        val fields = mutableListOf<PDField>()
        val nameCellId = firstCellId + 2
        val birthDateCellId = firstCellId + 4
        val startCellId = firstCellId + 5
        val endCellId = firstCellId + 6
        val daysCellId = firstCellId + 7
        pdfHelper.fillField(form, "Texteingabe$nameCellId", "${attendee.lastName}, ${attendee.firstName}", page)
            ?.let { fields.add(it) }
        pdfHelper.fillField(
            form,
            "Texteingabe$birthDateCellId",
            pdfHelper.formatBirthday(attendee.birthday, germanDate),
            page
        )?.let { fields.add(it) }
        pdfHelper.fillField(form, "Texteingabe$startCellId", settings.eventStart.format(germanDate), page)
            ?.let { fields.add(it) }
        pdfHelper.fillField(form, "Texteingabe$endCellId", settings.eventEnd.format(germanDate), page)
            ?.let { fields.add(it) }
        pdfHelper.fillField(form, "Texteingabe$daysCellId", "$DAYS_OF_EVENT", page)?.let { fields.add(it) }
        return fields
    }


}