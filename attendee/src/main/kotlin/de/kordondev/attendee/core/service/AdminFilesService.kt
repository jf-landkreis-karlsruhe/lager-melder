package de.kordondev.attendee.core.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.lowagie.text.*
import com.lowagie.text.List
import com.lowagie.text.pdf.*
import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.model.Department
import de.kordondev.attendee.core.persistence.entry.Food
import de.kordondev.attendee.core.persistence.entry.TShirtSize
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.awt.Color
import java.io.ByteArrayOutputStream


@Service
class AdminFilesService(
    private val resourceLoader: ResourceLoader,
    private val attendeeService: AttendeeService,
    private val eventService: EventService,
    private val departmentService: DepartmentService
) {
    private val yDistanceBetweenBatches = 141F
    fun createBatches(): ByteArray {
        val pageStream = ByteArrayOutputStream()
        val documentStream = ByteArrayOutputStream()
        val document = Document(PageSize.A4)
        val pdfCopy = PdfCopy(document, documentStream)
        document.open()

        val attendees = attendeeService.getAttendees().toList()
        var attendeeIndex = 0
        while (attendeeIndex < attendees.size) {

            val resource = resourceLoader.getResource("classpath:data/batch.pdf")
            val pdfReader = PdfReader(resource.inputStream)
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

    fun addName(content: PdfContentByte, attendee: Attendee, attendeesOnPage: Int) {
        content.beginText()
        val xValue = 345F
        val yValue = 738F
        content.setTextMatrix(xValue, yValue - yDistanceBetweenBatches * attendeesOnPage)
        content.showText("${attendee.firstName} ${attendee.lastName}")
        content.endText()
    }

    fun addDepartment(content: PdfContentByte, attendee: Attendee, attendeesOnPage: Int) {
        content.beginText()
        val xValue = 335F
        val yValue = 723F
        content.setTextMatrix(xValue, yValue - yDistanceBetweenBatches * attendeesOnPage)
        content.showText(attendee.department.name)
        content.endText()
    }

    fun addBarCode(content: PdfContentByte, attendee: Attendee, attendeesOnPage: Int) {
        val barcode = Barcode128()
        barcode.code = attendee.code
        barcode.barHeight = 40F
        barcode.x = 1.5F
        barcode.altText = attendee.code
        barcode.baseline = 12F
        barcode.size = 12F
        val template = barcode.createTemplateWithBarcode(content, Color.BLACK, Color.BLACK)
        val xValue = 320F
        val yValue = 660F
        content.addTemplate(template, xValue, yValue - yDistanceBetweenBatches * attendeesOnPage)
    }

    fun createEventPDF(frontendBaseUrl: String): ByteArray {
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

    fun createEventCode(eventCode: String): ByteArray {
        val out = ByteArrayOutputStream()
        val matrix: BitMatrix = MultiFormatWriter().encode(eventCode, BarcodeFormat.QR_CODE, 400, 400)
        MatrixToImageWriter.writeToStream(matrix, "png", out)
        return out.toByteArray()
    }

    fun createEventUrl(frontendBaseUrl: String, eventCode: String): String {
        if (frontendBaseUrl.endsWith("/", true)) {
            return "$frontendBaseUrl$eventCode"
        }
        return "$frontendBaseUrl/$eventCode"
    }

    fun createTShirtPDF(): ByteArray {
        val out = ByteArrayOutputStream()

        val document = Document(PageSize.A4)
        val writer = PdfWriter.getInstance(document, out)

        writer.isCloseStream = false
        document.open()
        val departments = departmentService.getDepartments()
        for (department in departments) {
            addTShirtsForDepartment(department, document)
        }
        document.close()
        return out.toByteArray()
    }

    private fun addTShirtsForDepartment(department: Department, document: Document) {
        val attendees = attendeeService.getAttendeesForDepartment(department)
        if (attendees.isNotEmpty()) {
            val tShirtCount = mutableMapOf<TShirtSize, Int>()
            for (attendee in attendees) {
                val currentCount = tShirtCount[attendee.tShirtSize] ?: 0
                tShirtCount[attendee.tShirtSize] = (currentCount + 1)
            }

            val font = Font(Font.TIMES_ROMAN, 20F, Font.NORMAL, Color.BLACK)
            document.add(Paragraph("Kreiszeltlager - T-Shirt", font))
            document.add(Paragraph("Abteilung: ${department.name}", font))

            val table = Table(2)
            table.borderWidth = 1F
            table.borderColor = Color(0, 0, 0)
            table.padding = 7F
            table.spacing = 0F
            table.addCell("Größe:")
            table.addCell("Anzahl:")
            table.endHeaders()
            for (size in TShirtSize.values()) {
                table.addCell(size.toString())
                table.addCell("${tShirtCount[size] ?: 0}")
            }
            document.add(table)
            document.newPage()
        }
    }

    fun createFoodPDF(): ByteArray {
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
            if (food != Food.MEAT && food != Food.SPECIAL) {
                document.add(Paragraph("${food.toString()} (${foodAttendees[food]!!.size})", headlineFont))
                val list = List()
                list.setListSymbol("\u2022")
                for (att in foodAttendees[food]!!) {
                    list.add(" ${att.firstName} ${att.lastName} aus ${att.department.name}")
                }
                document.add(list)
            }
            if (food == Food.SPECIAL) {
                document.add(Paragraph("${food.toString()} (${foodAttendees[food]!!.size})", headlineFont))
                val list = List()
                list.setListSymbol("\u2022")
                for (att in foodAttendees[food]!!) {
                    list.add(" ${att.firstName} ${att.lastName} aus ${att.department.name}. Kommentar: ${att.additionalInformation}. (Jugendwart: ${att.department.leaderEMail}, EMail: ${att.department.leaderEMail})")
                }
                document.add(list)
            }
        }

        document.close()
        return out.toByteArray()
    }

}