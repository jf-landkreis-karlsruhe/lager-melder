package de.kordondev.attendee.core.pdf

import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.persistence.entry.AttendeeRole
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import org.apache.pdfbox.pdmodel.interactive.form.PDField
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service

@Service
class YouthLeader(
        val resourceLoader: ResourceLoader,
        val pdfHelper: PDFHelper,
        @Value("\${data.kreiszeltlager.from}") private val eventStart: String,
        @Value("\${data.kreiszeltlager.to}") private val eventEnd: String,
        @Value("\${data.kreiszeltlager.year}") private val dataYear: String,
        @Value("\${data.kreiszeltlager.organisationAddress}") private val dataOrganisationAddress: String,
        @Value("\${data.kreiszeltlager.listYouthLeader.moneyProYouthLeader}") private val dataMoneyProYouthLeader: String
) {
    private val logger: Logger = LoggerFactory.getLogger(YouthLeader::class.java)

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

    fun createYouthLeaderPdf(attendees: List<Attendee>): PDDocument {
        val resource: Resource = resourceLoader.getResource("classpath:data/youthLeader.pdf")

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
            fields.addAll(fillPage(pdfDocument, attendeesForPage, TABLE_ROW_START, page))
            result.addPage(pdfDocument.getPage(0))
            page++
        }

        val finalForm = PDAcroForm(result)
        result.documentCatalog.acroForm = finalForm
        finalForm.fields = fields
        finalForm.needAppearances = true
        return result
    }


    fun fillPage(pdfDocument: PDDocument, attendees: List<Attendee>, cellIds: List<Int>, page: Int): MutableList<PDField> {
        val fields = mutableListOf<PDField>()
        val form = pdfDocument.documentCatalog.acroForm;

        pdfHelper.fillField(form, YEAR, dataYear, page)?.let { fields.add(it) }
        pdfHelper.fillField(form, MONEY_PRO_YOUTH_LEADER, dataMoneyProYouthLeader, page)?.let { fields.add(it) }
        pdfHelper.fillField(form, ORGANISATION_ADDRESS, dataOrganisationAddress, page)?.let { fields.add(it) }
        // pdfHelper.fillField(form, SELECT_HEIMFREIZEIT_ZELTLAGER, dataYear, page)?.let { fields.add(it) }

        for (i in attendees.indices) {
            fields.addAll(fillAttendeeInForm(attendees[i], form, cellIds[i], page))
        }
        return fields
    }

    fun fillAttendeeInForm(attendee: Attendee, form: PDAcroForm, cellId: Int, page: Int): List<PDField> {
        val fields = mutableListOf<PDField>()
        val startDateCell = if (cellId == 1) { "$START_DATE$cellId" } else { "${START_DATE}_$cellId" }
        pdfHelper.fillField(form, "$COUNT$cellId", "${(page - 1) * 5 + cellId}", page)?.let { fields.add(it) }
        pdfHelper.fillField(form, "$NAME_AND_BIRTHDAY$cellId", "${attendee.lastName}, ${attendee.firstName}, ${attendee.birthday}", page)?.let { fields.add(it) }
        pdfHelper.fillField(form, startDateCell, eventStart, page)?.let { fields.add(it) }
        pdfHelper.fillField(form, "$END_DATE$cellId", eventEnd, page)?.let { fields.add(it) }
        pdfHelper.fillField(form, "$DURATION$cellId", "$DAYS_OF_EVENT", page)?.let { fields.add(it) }
        return fields
    }
}