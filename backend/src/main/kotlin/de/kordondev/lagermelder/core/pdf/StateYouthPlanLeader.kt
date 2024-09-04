package de.kordondev.lagermelder.core.pdf

import de.kordondev.lagermelder.Helper
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
class StateYouthPlanLeader(
    val resourceLoader: ResourceLoader,
    val pdfHelper: PDFHelper,
    private val settingsService: SettingsService
) {
    private val logger: Logger = LoggerFactory.getLogger(StateYouthPlanLeader::class.java)

    val YEAR = "Landesjugendplan_"
    val MONEY_PRO_YOUTH_LEADER = "bewilligter_Zuschuss_"
    val ORGANISATION_ADDRESS = "Anschrift_und_Telefonnumm"
    val SELECT_HEIMFREIZEIT_ZELTLAGER = "Ankreuzfeld5" // 5, 6 or 7
    val COUNT = "Lfd"
    val NAME_AND_BIRTHDAY = "Betreuer_"
    val START_DATE = "Beginn" // with _ for all except the first one
    val END_DATE = "Ende_"
    val DURATION = "Einsatztage_"
    val ATTENDEES_ON_PAGE = 5
    val TABLE_ROW_START = listOf(1, 2, 3, 4, 5)

    val DAYS_OF_EVENT = 5

    fun createStateYouthPlanLeaderPdf(attendees: List<Attendee>): PDDocument {
        val resource: Resource = resourceLoader.getResource("classpath:data/stateYouthPlanLeader.pdf")
        val settings = settingsService.getSettings()

        val youthLeaders = attendees.filter { it.role == AttendeeRole.YOUTH_LEADER }
        logger.info("youthLeaders ${youthLeaders.size}")
        val result = PDDocument()
        val fields = mutableListOf<PDField>()

        var page = 1
        for (i in youthLeaders.indices step ATTENDEES_ON_PAGE) {

            val pdfDocument = PDDocument.load(resource.inputStream)

            val attendeesForPage = if (youthLeaders.size <= i + ATTENDEES_ON_PAGE) {
                youthLeaders.subList(i, youthLeaders.size)
            } else {
                youthLeaders.subList(i, i + ATTENDEES_ON_PAGE)
            }
            fields.addAll(fillPage(pdfDocument, attendeesForPage, TABLE_ROW_START, page, settings))
            result.addPage(pdfDocument.getPage(0))
            page++
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
        settings: SettingsEntry
    ): MutableList<PDField> {
        val fields = mutableListOf<PDField>()
        val form = pdfDocument.documentCatalog.acroForm;

        pdfHelper.fillField(form, YEAR, settings.eventStart.year.toString(), page)?.let { fields.add(it) }
        pdfHelper.fillField(form, MONEY_PRO_YOUTH_LEADER, settings.moneyPerYouthLoader, page)?.let { fields.add(it) }
        pdfHelper.fillField(form, ORGANISATION_ADDRESS, settings.organisationAddress, page)?.let { fields.add(it) }
        // pdfHelper.fillField(form, SELECT_HEIMFREIZEIT_ZELTLAGER, dataYear, page)?.let { fields.add(it) }

        for (i in attendees.indices) {
            fields.addAll(fillAttendeeInForm(attendees[i], form, cellIds[i], page, settings))
        }
        return fields
    }

    fun fillAttendeeInForm(
        attendee: Attendee,
        form: PDAcroForm,
        cellId: Int,
        page: Int,
        settings: SettingsEntry
    ): List<PDField> {
        val fields = mutableListOf<PDField>()
        val startDateCell = if (cellId == 1) {
            "$START_DATE$cellId"
        } else {
            "${START_DATE}_$cellId"
        }
        pdfHelper.fillField(form, "$COUNT$cellId", "${(page - 1) * 5 + cellId}", page)?.let { fields.add(it) }
        pdfHelper.fillField(
            form,
            "$NAME_AND_BIRTHDAY$cellId",
            "${attendee.lastName}, ${attendee.firstName}, ${
                pdfHelper.formatBirthday(
                    Helper.getBirthday(attendee),
                    germanDate
                )
            }",
            page
        )?.let { fields.add(it) }
        pdfHelper.fillField(form, startDateCell, settings.eventStart.format(germanDate), page)?.let { fields.add(it) }
        pdfHelper.fillField(form, "$END_DATE$cellId", settings.eventStart.format(germanDate), page)
            ?.let { fields.add(it) }
        pdfHelper.fillField(form, "$DURATION$cellId", "$DAYS_OF_EVENT", page)?.let { fields.add(it) }
        return fields
    }
}