package de.kordondev.lagermelder.core.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.lowagie.text.*
import com.lowagie.text.List
import com.lowagie.text.pdf.*
import de.kordondev.lagermelder.Helper
import de.kordondev.lagermelder.core.model.Attendee
import de.kordondev.lagermelder.core.model.Department
import de.kordondev.lagermelder.core.persistence.entry.Food
import de.kordondev.lagermelder.core.persistence.entry.TShirtSize
import de.kordondev.lagermelder.core.security.AuthorityService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.time.LocalDate


@Service
class AdminFilesService(
    private val resourceLoader: ResourceLoader,
    private val attendeeService: AttendeeService,
    private val eventService: EventService,
    private val departmentService: DepartmentService,
    private val authorityService: AuthorityService,
    private val settingsService: SettingsService,
) {
    private val yDistanceBetweenBatches = 141F
    private val logger: Logger = LoggerFactory.getLogger(AdminFilesService::class.java)
    fun createBatches(): ByteArray {
        authorityService.isSpecializedFieldDirector()
        val documentStream = ByteArrayOutputStream()
        val document = Document(PageSize.A4)
        val pdfCopy = PdfCopy(document, documentStream)
        document.open()

        val attendees = attendeeService.getAttendees()
        logger.info("Creating batches for ${attendees.size} on ${1 + (attendees.size / 5)} pages")
        var attendeeIndex = 0
        while (attendeeIndex < attendees.size) {

            val resource = resourceLoader.getResource("classpath:data/batch.pdf")
            val pdfReader = PdfReader(resource.inputStream)
            val pageStream = ByteArrayOutputStream()
            val stamper = PdfStamper(pdfReader, pageStream)

            val content = stamper.getOverContent(1)
            content.setColorFill(Color.BLACK)
            content.setFontAndSize(
                BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED),
                16F
            )

            var attendeesOnPage = 0
            while (attendeeIndex < attendees.size && attendeesOnPage < 5) {
                addName(content, attendees[attendeeIndex], attendeesOnPage)
                addDepartment(content, attendees[attendeeIndex], attendeesOnPage)
                addBarCode(content, attendees[attendeeIndex], attendeesOnPage)

                attendeesOnPage++
                attendeeIndex++
            }

            stamper.close()
            pdfReader.close()

            val readInputPDF = PdfReader(pageStream.toByteArray())

            pdfCopy.addPage(pdfCopy.getImportedPage(readInputPDF, 1))
            pdfCopy.freeReader(readInputPDF)
        }

        document.close()
        pdfCopy.close()

        return documentStream.toByteArray()
    }

    private fun addName(content: PdfContentByte, attendee: Attendee, attendeesOnPage: Int) {
        content.beginText()
        val xValue = 345F
        val yValue = 738F
        content.setTextMatrix(xValue, yValue - yDistanceBetweenBatches * attendeesOnPage)
        content.showText("${attendee.firstName} ${attendee.lastName}")
        content.endText()
    }

    private fun addDepartment(content: PdfContentByte, attendee: Attendee, attendeesOnPage: Int) {
        content.beginText()
        val xValue = 335F
        val yValue = 723F
        content.setTextMatrix(xValue, yValue - yDistanceBetweenBatches * attendeesOnPage)
        content.showText(attendee.department.name)
        content.endText()
    }

    private fun addBarCode(content: PdfContentByte, attendee: Attendee, attendeesOnPage: Int) {
        val barcode = Barcode128()
        barcode.code = attendee.code
        barcode.barHeight = 40F
        barcode.x = 1.4F
        barcode.altText = attendee.code
        barcode.baseline = 12F
        barcode.size = 12F
        val template = barcode.createTemplateWithBarcode(content, Color.BLACK, Color.BLACK)
        val xValue = 320F
        val yValue = 660F
        content.addTemplate(template, xValue, yValue - yDistanceBetweenBatches * attendeesOnPage)
    }

    fun createEventPDF(frontendBaseUrl: String): ByteArray {
        authorityService.isSpecializedFieldDirector()
        val out = ByteArrayOutputStream()

        val document = Document(PageSize.A4)
        val writer = PdfWriter.getInstance(document, out)

        writer.isCloseStream = false

        val font = Font(
            BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED),
            20F,
            Font.BOLD,
            Color.BLACK
        )

        val events = eventService.getEvents()

        for (event in events) {
            val headline = Paragraph(event.name, font)
            headline.alignment = Element.ALIGN_CENTER
            headline.spacingAfter = 50F

            document.open()
            document.add(headline)
            document.add(Paragraph("Bitte beim Kommen und Gehen ein und ausloggen.")) //TODO: Correct sentence


            val qrCode = Image.getInstance(createEventCode(createEventUrl(frontendBaseUrl, event.code)))
            qrCode.scaleToFit(PageSize.A4.width, PageSize.A4.height)
            val x = (PageSize.A4.width - qrCode.scaledWidth) / 2
            val y = (PageSize.A4.height - qrCode.scaledHeight) / 2
            qrCode.setAbsolutePosition(x, y)
            document.add(qrCode)
            document.newPage()
        }

        document.close()
        return out.toByteArray()
    }

    private fun createEventCode(eventCode: String): ByteArray {
        val out = ByteArrayOutputStream()
        val matrix: BitMatrix = MultiFormatWriter().encode(eventCode, BarcodeFormat.QR_CODE, 400, 400)
        MatrixToImageWriter.writeToStream(matrix, "png", out)
        return out.toByteArray()
    }

    private fun createEventUrl(frontendBaseUrl: String, eventCode: String): String {
        if (frontendBaseUrl.endsWith("/", true)) {
            return "$frontendBaseUrl$eventCode"
        }
        return "$frontendBaseUrl/$eventCode"
    }

    fun createTShirtPDF(): ByteArray {
        authorityService.isSpecializedFieldDirector()
        val out = ByteArrayOutputStream()

        val document = Document(PageSize.A4)
        val writer = PdfWriter.getInstance(document, out)

        writer.isCloseStream = false
        document.open()

        val globalDepartments = Department(0, "Zeltlager gesamt", "", "")
        val allAttendees = attendeeService.getAttendees()
        val totalTShirtCount = countTShirtPerSize(allAttendees)
        val eventStart = settingsService.getSettings().eventStart
        val totalBraceletCount = countBracelet(allAttendees, eventStart)

        addTShirtsAndBraceletForDepartment(globalDepartments, totalTShirtCount, totalBraceletCount, document)

        val departments = departmentService.getDepartments()
        for (department in departments) {
            val attendees = attendeeService.getAttendeesForDepartment(department)
            if (attendees.isNotEmpty()) {
                val tShirtCount = countTShirtPerSize(attendees)
                val braceletCount = countBracelet(attendees, eventStart)
                addTShirtsAndBraceletForDepartment(department, tShirtCount, braceletCount, document)
            }
        }
        document.close()
        return out.toByteArray()
    }

    private fun addTShirtsAndBraceletForDepartment(
        department: Department,
        tShirtCount: MutableMap<TShirtSize, Int>,
        braceletCount: MutableMap<Color, Int>,
        document: Document
    ) {
        val headlineFont = Font(Font.TIMES_ROMAN, 20F, Font.NORMAL, Color.BLACK)
        document.add(Paragraph("Abteilung: ${department.name}", headlineFont))
        document.add(Paragraph("Kreiszeltlager - T-Shirt", headlineFont))

        val table = Table(2)
        table.borderWidth = 1F
        table.borderColor = Color(0, 0, 0)
        table.padding = 5F
        table.spacing = 0F
        table.addCell("Größe:")
        table.addCell("Anzahl:")
        table.endHeaders()
        for (size in TShirtSize.values()) {
            table.addCell(size.toString())
            table.addCell("${tShirtCount[size] ?: 0}")
        }
        document.add(table)

        document.add(Paragraph("Kreiszeltlager - Armband", headlineFont))
        val braceletTable = Table(2)
        braceletTable.borderWidth = 1F
        braceletTable.borderColor = Color(0, 0, 0)
        braceletTable.padding = 5F
        braceletTable.spacing = 0F
        braceletTable.addCell("Farbe:")
        braceletTable.addCell("Anzahl:")
        braceletTable.endHeaders()

        for (color in listOf(Color.GREEN, Color.YELLOW, Color.RED)) {
            braceletTable.addCell(colorToString(color))
            braceletTable.addCell("${braceletCount[color] ?: 0}")
        }
        document.add(braceletTable)

        document.newPage()
    }

    private fun countTShirtPerSize(attendees: kotlin.collections.List<Attendee>): MutableMap<TShirtSize, Int> {
        val tShirtCount = mutableMapOf<TShirtSize, Int>()
        for (attendee in attendees) {
            val currentCount = tShirtCount[attendee.tShirtSize] ?: 0
            tShirtCount[attendee.tShirtSize] = (currentCount + 1)
        }
        return tShirtCount
    }

    private fun countBracelet(
        attendees: kotlin.collections.List<Attendee>,
        eventStart: LocalDate
    ): MutableMap<Color, Int> {
        val braceletCount = mutableMapOf<Color, Int>()
        for (attendee in attendees) {
            val color = colorForAgeGroup(attendee, eventStart)
            val currentCount = braceletCount[color] ?: 0
            braceletCount[color] = (currentCount + 1)
        }
        return braceletCount
    }

    private fun colorForAgeGroup(attendee: Attendee, eventStart: LocalDate): Color {
        val age = Helper.ageAtEvent(attendee.birthday, eventStart)
        if (age < 16) {
            return Color.RED
        }
        if (age < 18) {
            return Color.YELLOW
        }
        return Color.GREEN
    }

    private fun colorToString(color: Color): String {
        return when (color) {
            Color.GREEN -> "Grün"
            Color.YELLOW -> "Gelb"
            Color.RED -> "Rot"
            else -> color.toString()
        }
    }


    fun createFoodPDF(): ByteArray {
        authorityService.isSpecializedFieldDirector()
        val out = ByteArrayOutputStream()

        val document = Document(PageSize.A4)
        val writer = PdfWriter.getInstance(document, out)

        val headlineFont = Font(Font.TIMES_ROMAN, 20F, Font.NORMAL, Color.BLACK)
        writer.isCloseStream = false
        document.open()
        val attendees = attendeeService.getAttendees()
        val foodAttendees = mutableMapOf<Food, MutableList<Attendee>>()
        for (food in Food.values()) {
            foodAttendees[food] = mutableListOf()
        }

        for (attendee in attendees) {
            foodAttendees[attendee.food]?.add(attendee)
        }

        document.add(Paragraph("Kreiszeltlager - Essen", headlineFont))
        for (food in Food.values()) {
            document.add(Paragraph("${food.toString()} (${foodAttendees[food]!!.size})", headlineFont))
            if (food != Food.MEAT) {
                val list = List()
                list.setListSymbol("\u2022")
                for (att in foodAttendees[food]!!) {
                    if (food == Food.SPECIAL) {
                        list.add(" ${att.firstName} ${att.lastName} aus ${att.department.name}. Kommentar: ${att.additionalInformation}. (Jugendwart: ${att.department.leaderEMail}, EMail: ${att.department.leaderEMail})")
                    } else {
                        list.add(" ${att.firstName} ${att.lastName} aus ${att.department.name}")
                    }
                }
                document.add(list)
            }
        }

        document.close()
        return out.toByteArray()
    }

    fun createOverviewForEachDepartment(): ByteArray {
        authorityService.isSpecializedFieldDirector()
        val eventStart = settingsService.getSettings().eventStart
        val out = ByteArrayOutputStream()

        val document = Document(PageSize.A4)
        val writer = PdfWriter.getInstance(document, out)
        val headlineFont = Font(Font.TIMES_ROMAN, 20F, Font.NORMAL, Color.BLACK)

        writer.isCloseStream = false
        document.open()

        val departments = departmentService.getDepartments()
        for (department in departments) {
            val attendees = attendeeService.getAttendeesForDepartment(department)
            if (attendees.isEmpty()) {
                continue
            }
            document.add(Paragraph("Abteilung: ${department.name}", headlineFont))

            val table = Table(3)
            table.borderWidth = 1F
            table.borderColor = Color(0, 0, 0)
            table.padding = 3F
            table.spacing = 0F
            table.addCell("Name:")
            table.addCell("T-Shirt")
            table.addCell("Armband")
            table.endHeaders()
            for (attendee in attendees) {
                table.addCell("${attendee.firstName} ${attendee.lastName}")
                table.addCell(attendee.tShirtSize.toString())
                table.addCell(colorToString(colorForAgeGroup(attendee, eventStart)))
            }
            document.add(table)
            document.newPage()
        }
        document.close()
        return out.toByteArray()
    }

}